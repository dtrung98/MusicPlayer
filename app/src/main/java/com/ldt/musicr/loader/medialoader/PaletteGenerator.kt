package com.ldt.musicr.loader.medialoader

import android.content.Context
import com.ldt.musicr.utils.ArtworkUtils.getBitmapRequestBuilder
import android.graphics.Bitmap
import com.ldt.musicr.App
import com.ldt.musicr.service.MusicPlayerRemote
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.res.ResourcesCompat
import com.ldt.musicr.R
import androidx.palette.graphics.Palette
import com.ldt.musicr.provider.ColorProvider
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import com.ldt.musicr.interactors.AppExecutors
import com.ldt.musicr.util.BitmapEditor
import com.ldt.musicr.util.Tool
import java.lang.Exception

class PaletteGenerator {
    private var isCancelled = false
    fun cancel() {
        isCancelled = true
    }

    fun run() {
        AppExecutors.io().execute {
            runCatching { runInternal() }.onFailure { it.printStackTrace() }
        }
    }

    private fun runInternal() {
        var bitmap: Bitmap? = null
        val context: Context = App.getInstance() ?: return
        try {
            val song = MusicPlayerRemote.getCurrentSong()
            bitmap = getBitmapRequestBuilder(context, song).submit().get()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (bitmap == null) {
            val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.speaker2, context.theme) as? BitmapDrawable
            bitmap = drawable?.bitmap
        }

        bitmap ?: return

        val color = getMostColor(bitmap)
        Tool.setMostCommonColor(color)
        Tool.setSurfaceColor(color)
        val palette = Palette.from(bitmap).generate()
        val outColors = IntArray(2)
        val outAlphas = FloatArray(2)
        val generated = generatedPalette(palette, outColors, outAlphas)
        if (!isCancelled && generated) {

            // re-assign colors
            Tool.ColorOne = outColors[0]
            Tool.ColorTwo = outColors[1]
            Tool.AlphaOne = outAlphas[0]
            Tool.AlphaTwo = outAlphas[1]

            // reset color provider
            ColorProvider.paletteRelatedLM.reset()
            ColorProvider.darkLightRelatedLM.reset()

            // notify whole application
            val intent = Intent(PALETTE_CHANGED)
            intent.putExtra(RESULT, true)
            intent.putExtra(COLOR_ONE, outColors[0])
            intent.putExtra(COLOR_TWO, outColors[1])
            intent.putExtra(ALPHA_ONE, outAlphas[0])
            intent.putExtra(ALPHA_TWO, outAlphas[1])
            context.sendBroadcast(intent)
        }
    }

    private fun generatedPalette(p: Palette, outColors: IntArray, outAlphas: FloatArray): Boolean {
        val palette = IntArray(6)
        // access palette colors here
        val psVibrant = p.vibrantSwatch
        val psVibrantLight = p.lightVibrantSwatch
        val psVibrantDark = p.darkVibrantSwatch
        val psMuted = p.mutedSwatch
        val psMutedLight = p.lightMutedSwatch
        val psMutedDark = p.darkMutedSwatch
        for (i in 0..5) palette[i] = 0
        if (psVibrant != null) {
            palette[0] = psVibrant.rgb
        }
        if (psVibrantLight != null) {
            palette[1] = psVibrantLight.rgb
        }
        if (psVibrantDark != null) {
            palette[2] = psVibrantDark.rgb
        }
        if (psMuted != null) {
            palette[3] = psMuted.rgb
        }
        if (psMutedLight != null) {
            palette[4] = psMutedLight.rgb
        }
        if (psMutedDark != null) {
            palette[5] = psMutedDark.rgb
        }
        val hsv = FloatArray(3)
        Color.colorToHSV(Tool.getMostCommonColor(), hsv)
        //     Log.d(hsv[0] + "|" + hsv[1] + "|" + hsv[2], "ColorMe");
        val alpha7basic = hsv[1]
        val color1: Int
        val color2: Int
        val alpha1: Float
        val alpha2: Float
        if (alpha7basic < 0.5f) //  Đủ đậm thì màu mostCommon sẽ là màu song name, màu basic là màu artist
        {
            color1 = Tool.getMostCommonColor()
            alpha1 = 1f
            color2 = Tool.getBaseColor()
            alpha2 = alpha7basic
        } else  // ngược lại thì màu basic sẽ là màu song name
        {
            val tempColor1 = getBestColorFromPalette(palette)
            color1 = if (tempColor1 == 0) Tool.getBaseColor() else tempColor1
            alpha1 = 1f
            color2 = Color.WHITE
            alpha2 = 0.7f
        }
        outColors[0] = color1
        outColors[1] = color2
        outAlphas[0] = alpha1
        outAlphas[1] = alpha2
        return true
    }

    private fun getBestColorFromPalette(palette: IntArray): Int {
        val c: Int
        val hsv = FloatArray(3)
        val list = intArrayOf(palette[2], palette[0], palette[5], palette[1], palette[3], palette[4])
        // theo thứ tự : 2 - 0 - 5 -1 - 3 - 4
        for (i in 0..5) {
            Color.colorToHSV(list[i], hsv)
            if (hsv[1] >= 0.5f) {
                c = list[i]
                return c
            }
        }
        return 0
    }

    private fun getMostColor(origin: Bitmap): Int {
        val options = BitmapFactory.Options()
        options.inSampleSize = 38
        var sample: Bitmap? = BitmapEditor.getResizedBitmap(origin, origin.height / 38, origin.width / 38)
        sample = BitmapEditor.updateSat(sample, 4f)
        sample = BitmapEditor.fastblur(sample, 1f, 4)
        val averageColorRGB = BitmapEditor.getAverageColorRGB(sample)

        //black_theme = BitmapEditor.PerceivedBrightness(95, averageColorRGB);
        //Bitmap mBlurArtWork = sample;
        return Color.argb(255, averageColorRGB[0], averageColorRGB[1], averageColorRGB[2])
    }

    companion object {
        const val PALETTE_CHANGED = "com.ldt.musicr.PALETTE_CHANGED"
        const val COLOR_ONE = "COLOR_ONE"
        const val COLOR_TWO = "COLOR_TWO"
        const val ALPHA_ONE = "ALPHA_ONE"
        const val ALPHA_TWO = "ALPHA_TWO"
        const val RESULT = "RESULT"
    }
}