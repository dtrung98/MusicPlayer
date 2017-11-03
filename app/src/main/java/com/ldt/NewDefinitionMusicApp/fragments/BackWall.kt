package com.ldt.NewDefinitionMusicApp.fragments


import android.app.Activity
import android.app.WallpaperManager
import android.graphics.Bitmap

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout


import com.ldt.NewDefinitionMusicApp.InternalTools.ImageEditor
import com.ldt.NewDefinitionMusicApp.R
import com.ldt.NewDefinitionMusicApp.activities.SupportFragmentActivity

/**
 * A simple [Fragment] subclass.
 */
class BackWall : FragmentPlus() {
    override fun setDefaultStatusTheme() : StatusTheme{
        return StatusTheme.BlackIcon
    }

    override fun IWantApplyMargin(): ApplyMargin {
        return  ApplyMargin.NONE
    }

    private lateinit var iV_wallpaper: ImageView
   private lateinit var activity: SupportFragmentActivity
   private lateinit var blank_wall_back: View
   private lateinit var draw_on: LinearLayout
    private lateinit var trade_mark: View
    private fun MergeUI() {
        activity = getActivity() as SupportFragmentActivity
        iV_wallpaper = ImageView(activity)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (rootView as FrameLayout).addView(iV_wallpaper, 0, params)
        blank_wall_back = rootView.findViewById(R.id.back_wall_blank)
        draw_on = rootView.findViewById(R.id.back_wall_drawn_on)
        trade_mark = rootView.findViewById(R.id.trade_mark)
    }

    private fun setAllClick() {
        val views = arrayOf(// put views here
                blank_wall_back, trade_mark)

        for (va in views) va.setOnClickListener(Onclick)
    }

    private val Onclick = View.OnClickListener { v ->
        val id = v.id
        when (id) {
            R.id.trade_mark, R.id.back_wall_blank -> {
                back_wall_blank_onTouch()
                return@OnClickListener
            }
        }
    }

    private fun back_wall_blank_onTouch() {
        (getActivity() as SupportFragmentActivity).pushFragment(HelloScreenFragment.Initialize(getActivity()), true)
    }

    private val wallPaper: Bitmap
        get() {
            val wallpaperManager = WallpaperManager.getInstance(getActivity())
            val wallpaperDrawable = wallpaperManager.drawable
            val bmp = (wallpaperDrawable as BitmapDrawable).bitmap
            return if (bmp.width > 0) bmp.copy(bmp.config, true) else Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        }

    private fun applyBackWallEffect4Wallpaper(old: Bitmap): Bitmap {
//     Canvas canvas = new Canvas(blur);
        //    Paint paint= new Paint();
        //   int w=blur.getWidth(),h = blur.getHeight();
        //   paint.setARGB(100,0,0,0);
        //   canvas.drawRect(0,0,w,h,paint);

        return ImageEditor.getBlurredWithGoodPerformance(getActivity(), old, 1, 12, 1.4f)
    }

    private fun createAnWallImage() {

        iV_wallpaper.scaleType = ImageView.ScaleType.CENTER_CROP
        val activity = getActivity() as SupportFragmentActivity
        iV_wallpaper.setImageBitmap(activity.getIV_Wallpaper())
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater!!.inflate(R.layout.fragment_back_wall, container, false)
        MergeUI()
        setAllClick()
       createAnWallImage()

        return rootView
    }

    override fun onTransitionComplete() {
        draw_on.visibility = View.VISIBLE
        blank_wall_back.visibility = View.GONE
    }

    companion object {
        fun Initialize(activity: Activity): BackWall {
            val fragment = BackWall()
            fragment.setFrameLayoutNTransitionType(activity, SupportFragmentActivity.TransitionType.FADE_IN_OUT)
            return fragment
        }
    }
}
