package com.ldt.musicr.fragments


import android.app.Activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout


import com.ldt.musicr.R
import com.ldt.musicr.activities.SupportFragmentPlusActivity

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

    private lateinit var imageView: ImageView
   private lateinit var activity: SupportFragmentPlusActivity
   private lateinit var blank_wall_back: View
   private lateinit var draw_on: LinearLayout
    private lateinit var trade_mark: View
    private fun MergeUI() {
        activity = getActivity() as SupportFragmentPlusActivity
        imageView = ImageView(activity)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        (rootView as FrameLayout).addView(imageView, 0, params)
        blank_wall_back = rootView.findViewById(R.id.back_wall_blank)
        draw_on = rootView.findViewById(R.id.back_wall_drawn_on)
        trade_mark = rootView.findViewById(R.id.trade_mark)
    }


    private fun createAnWallImage() {

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val activity = getActivity() as SupportFragmentPlusActivity
        activity.setBlurWallpaper(imageView)
    }
    private final val TAG="BackWall"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_back_wall, container, false)
        Log.d(TAG,"One")
        MergeUI()
        Log.d(TAG,"Two")
        Log.d(TAG,"Three")
       createAnWallImage()
        Log.d(TAG,"Four")
        return rootView
    }

    override fun onTransitionComplete() {
        Log.d(TAG,"Five")
        draw_on.visibility = View.VISIBLE
        Log.d(TAG,"Six")
        blank_wall_back.visibility = View.GONE
        Log.d(TAG,"Seven")
    }

    companion object {
        fun Initialize(activity: Activity): BackWall {
            val fragment = BackWall()
            fragment.setFrameLayoutNTransitionType(activity, SupportFragmentPlusActivity.TransitionType.FADE_IN_OUT)
            return fragment
        }
    }
}
