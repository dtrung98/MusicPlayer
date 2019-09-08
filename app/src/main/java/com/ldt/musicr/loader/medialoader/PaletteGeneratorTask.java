package com.ldt.musicr.loader.medialoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import android.util.Log;

import com.ldt.musicr.R;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.util.BitmapEditor;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.util.Util;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import static com.ldt.musicr.util.BitmapEditor.updateSat;

public class PaletteGeneratorTask extends AsyncTask<Void,Void,Boolean> {
    private static final String TAG = "PaletteGeneratorTask";
    public static final String PALETTE_ACTION = "com.ldt.musicr.loader.medialoader.PaletteGeneratorTask";
    public static final String COLOR_ONE = "color_1";
    public static final String COLOR_TWO = "color_2";
    public static final String ALPHA_ONE = "alpha_1";
    public static final String ALPHA_TWO = "alpha_2";
    public static final String RESULT ="result";

        private final WeakReference<Context> mWeakRefContext;
        private boolean mCanceled = false;

        public PaletteGeneratorTask(Context context) {
            mWeakRefContext = new WeakReference<>(context);
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean c;
            long start = System.currentTimeMillis();
            try {
                c = getColor();
            } catch (Exception ignore) {
                c = false;
            }
            long time1 = System.currentTimeMillis() - start;


            Log.d(TAG, "doInBackground: time1 = "+time1);

            return c;
        }

        private boolean getColor() {
            Bitmap bitmap;
            try {
                bitmap = Picasso.get().load(Util.getAlbumArtUri(MusicPlayerRemote.getCurrentSong().albumId)).error(R.drawable.speaker2).get();
            } catch (Exception e) {
                Context context = mWeakRefContext.get();
                if(context!=null)
                    bitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.speaker2)).getBitmap();
                else bitmap = null;
            }

            if(bitmap==null) return false;
            int color = getMostColor(bitmap);
            Tool.setMostCommonColor(color);
            Tool.setSurfaceColor(color);

            Palette palette = Palette.from(bitmap).generate();
            return !onGeneratedPalette(palette);
        }

        private boolean onGeneratedPalette(@NonNull Palette p) {
            int[] palette = new int[6];
            // access palette colors here
            Palette.Swatch psVibrant = p.getVibrantSwatch();
            Palette.Swatch psVibrantLight = p.getLightVibrantSwatch();
            Palette.Swatch psVibrantDark = p.getDarkVibrantSwatch();
            Palette.Swatch psMuted = p.getMutedSwatch();
            Palette.Swatch psMutedLight = p.getLightMutedSwatch();
            Palette.Swatch psMutedDark = p.getDarkMutedSwatch();

            for (int i = 0; i < 6; i++)
                palette[i] = 0;
            if (psVibrant != null) {
                palette[0] = psVibrant.getRgb();
            }
            if (psVibrantLight != null) {
                palette[1] = psVibrantLight.getRgb();
            }
            if (psVibrantDark != null) {
                palette[2] = psVibrantDark.getRgb();
            }
            if (psMuted != null) {
                palette[3] = psMuted.getRgb();
            }
            if (psMutedLight != null) {
                palette[4] = psMutedLight.getRgb();
            }
            if (psMutedDark != null) {
                palette[5] = psMutedDark.getRgb();
            }



            float[] hsv = new float[3];
            Color.colorToHSV(Tool.getMostCommonColor(), hsv);
            //     Log.d(hsv[0] + "|" + hsv[1] + "|" + hsv[2], "ColorMe");
            float alpha_7basic = hsv[1];

            final int color1,color2;
            float alpha1,alpha2;

            if(alpha_7basic<0.5f) //  Đủ đậm thì màu mostCommon sẽ là màu song name, màu basic là màu artist
            {
                color1= Tool.getMostCommonColor();
                alpha1 =1;
                color2 = Tool.getBaseColor();
                alpha2 =alpha_7basic;
            }
            else // ngược lại thì màu basic sẽ là màu song name
            {
                int tempColor1 = getBestColorFromPalette(palette);
                if(tempColor1==0) color1 = Tool.getBaseColor();
                else color1 = tempColor1;
                alpha1 = 1;
                color2 =Color.WHITE;
                alpha2 = 0.7f;
            }

            Context context = mWeakRefContext.get();

            if(context!=null&&!mCanceled) {
                Intent intent = new Intent(PALETTE_ACTION);
                intent.putExtra(RESULT, true);
                intent.putExtra(COLOR_ONE, color1);
                intent.putExtra(COLOR_TWO, color2);
                intent.putExtra(ALPHA_ONE, alpha1);
                intent.putExtra(ALPHA_TWO, alpha2);
                context.sendBroadcast(intent);
            }

            else return false;
            return true;
        }
        private int getBestColorFromPalette(int[] palette) {
            int c = 0;
            float[] hsv = new float[3];
            int[] list = new int[]{palette[2], palette[0], palette[5], palette[1], palette[3], palette[4]};
            // theo thứ tự : 2 - 0 - 5 -1 - 3 - 4
            for (int i = 0; i < 6; i++) {
                Color.colorToHSV(list[i], hsv);
                if(hsv[1]>=0.5f) {
                    c = list[i];
                    return c;
                }
            }
            return 0;
        }
        private int getMostColor(Bitmap origin) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 38;
            Bitmap sample;
            sample = BitmapEditor.getResizedBitmap(origin,origin.getHeight()/38,origin.getWidth()/38);

            sample = updateSat(sample, 4);
            sample = BitmapEditor.fastblur(sample, 1, 4);
            int[] averageColorRGB = BitmapEditor.getAverageColorRGB(sample);
            // black_theme = BitmapEditor.PerceivedBrightness(95, averageColorRGB);

            int  mColor24Bit = Color.argb(255,averageColorRGB[0],averageColorRGB[1],averageColorRGB[2]) ;


            Bitmap mBlurArtWork = sample;
            return mColor24Bit;
        }
        public void cancel() {
            mCanceled = true;
            cancel(true);
           mWeakRefContext.clear();
        }

    @Override
    protected void onPostExecute(Boolean aBoolean) {

    }
}