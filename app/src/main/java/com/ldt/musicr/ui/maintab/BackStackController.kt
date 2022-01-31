package com.ldt.musicr.ui.maintab

import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ldt.musicr.service.MusicServiceEventListener
import androidx.cardview.widget.CardView
import com.ldt.musicr.ui.widget.viewpager.GestureControlViewPager
import com.ldt.musicr.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import com.ldt.musicr.ui.MusicServiceActivity
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ldt.musicr.ui.CardLayerController.CardLayerAttribute
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ldt.musicr.ui.AppActivity
import com.ldt.musicr.ui.maintab.library.LibraryTabFragment
import com.ldt.musicr.ui.widget.navigate.BackPressableFragment
import com.ldt.musicr.loader.medialoader.ArtistLoader
import com.ldt.musicr.service.MusicPlayerRemote
import com.ldt.musicr.glide.ArtistGlideRequest
import com.ldt.musicr.glide.GlideApp
import com.ldt.musicr.App
import com.ldt.musicr.util.Tool
import java.util.ArrayList

class BackStackController : CardLayerFragment(), OnPageChangeListener, MusicServiceEventListener {
    private var mRoot: CardView? = null
    private lateinit var mDimView: View
    private lateinit var backgroundImageView: ImageView
    private lateinit var mViewPager: GestureControlViewPager
    private var mIsUsingAIAsBg = true
    private var mNavigationHeight = 0f
    private var mNavigationAdapter: BottomNavigationPagerAdapter? = null
    private var dpX1 = 1f
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        return inflater.inflate(R.layout.screen_card_layer_back_stack, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView(view)
        (activity as? MusicServiceActivity)?.addMusicServiceEventListener(this)
        dpX1 = resources.getDimension(R.dimen.oneDP)
        mNavigationHeight = view.resources.getDimension(R.dimen.bottom_navigation_height)
        mNavigationAdapter = BottomNavigationPagerAdapter(activity, childFragmentManager)
        mViewPager.adapter = mNavigationAdapter
        mViewPager.offscreenPageLimit = 3
        mViewPager.addOnPageChangeListener(this)
        mViewPager.isSwipeGestureEnabled = false
        mViewPager.setOnTouchListener { _: View?, event: MotionEvent? -> mCardLayerController.dispatchOnTouchEvent(mRoot, event) }
        onUsingArtistImagePreferenceChanged()
    }

    private fun bindView(view: View) {
        mRoot = view.findViewById(R.id.root)
        mDimView = view.findViewById(R.id.dim_view)
        backgroundImageView = view.findViewById(R.id.back_image)
        mViewPager = view.findViewById(R.id.view_pager)
    }

    fun streamOnTouchEvent(event: MotionEvent?): Boolean {
        return mCardLayerController.dispatchOnTouchEvent(mRoot, event)
    }

    override fun onLayerUpdate(attrs: ArrayList<CardLayerAttribute>, actives: ArrayList<Int>, me: Int) {
        if (mRoot == null) return
        if (me == 1) {
            var pc = attrs[actives[0]].runtimePercent
            if (pc < 0.01f) pc = 0f else if (pc > 1) pc = 1f
            mRoot!!.radius = dpX1 * 14 * pc
            mDimView.alpha = 0.4f * attrs[actives[0]].runtimePercent
            //  mRoot.setRoundNumber( attrs.get(actives.get(0)).getRuntimePercent(),false);
            mRoot?.alpha = 1f
        } else if (me != 0) {
            //  other, active_i >1
            // min = 0.3
            // max = 0.45
            val min = 0.3f
            val max = 0.65f
            val hieu = max - min
            val postFactor = (me - 1.0f) / (me - 0.75f) // 1/2, 2/3,3/4, 4/5, 5/6 ...
            val preFactor = (me - 2.0f) / (me - 0.75f) // 0/1, 1/2, 2/3, ...
            var darken = min + hieu * preFactor + hieu * (postFactor - preFactor) * attrs[actives[0]].runtimePercent
            // Log.d(TAG, "darken = " + darken);
            //  mRoot.setDarken(darken,false);
            if (darken < 0) darken = 0f
            if (darken > 1) darken = 1f
            mDimView.alpha = darken
            //   TabSwitcherFrameLayout.setDarken(0.3f + 0.6f*pcOnTopLayer,false);
            //  mRoot.setRoundNumber(1,true);
            mRoot!!.radius = dpX1 * 14
            if (me == actives.size - 1) mRoot!!.alpha = 1 - darken else mRoot!!.alpha = 1f
        }
        doTranslateNavigation(attrs, actives, me)
    }

    private fun doTranslateNavigation(attrs: ArrayList<CardLayerAttribute>, actives: ArrayList<Int>, me: Int) {
        if (bottomNavigationParent != null) {
            val bnpHeight = bottomNavigationParent!!.height
            if (me == 1) {
                var pc = attrs[actives[0]].runtimePercent
                if (pc > 1) pc = 1f else if (pc < 0) pc = 0f
                bottomNavigationParent!!.translationY = pc * bnpHeight
            } else if (me != 0) {
                bottomNavigationParent!!.translationY = bnpHeight.toFloat()
            }
        }
    }

    override fun onLayerHeightChanged(attr: CardLayerAttribute) {
        if (mRoot != null) {
            var pc = attr.mCurrentTranslate / attr.maxPosition
            if (pc > 1) pc = 1f else if (pc < 0) pc = 0f
            val scale = 0.2f + pc * 0.8f
            var radius = 1 - pc
            if (radius < 0.1f) radius = 0f else if (radius > 1) radius = 1f
            mRoot!!.radius = dpX1 * 14 * radius
            mRoot!!.alpha = scale
        }
    }

    override fun isFullscreenLayer(): Boolean {
        return true
    }

    override fun getLayerMinHeight(context: Context, maxHeight: Int): Int {
        return maxHeight
    }

    override fun getCardLayerTag(): String {
        return TAG
    }

    private var bottomNavigationView: BottomNavigationView? = null
    private var bottomNavigationParent: View? = null
    fun attachBottomNavigationView(activity: AppActivity) {
        bottomNavigationParent = activity.findViewById(R.id.bottom_navigation_parent)
        bottomNavigationView = bottomNavigationParent?.findViewById(R.id.bottom_navigation_view)
        bottomNavigationView?.setOnNavigationItemSelectedListener(mItemSelectedListener)
    }

    fun navigateToLibraryTab(go: Boolean): LibraryTabFragment? {
        val fragment = navigateToTab(1, go)
        return if (fragment is LibraryTabFragment) {
            fragment
        } else null
    }

    private fun navigateToTab(item: Int, go: Boolean): Fragment? {
        if (go) mViewPager.currentItem = item
        val fragment = mNavigationAdapter!!.getItem(1)
        if (fragment is BackPressableFragment) {
            fragment.popToRootFragment()
            return fragment.rootFragment
        }
        return null
    }

    private val mItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_feature -> {
                if (mViewPager.currentItem != 0) {
                    mViewPager.currentItem = 0
                    bringToTopThisTab(0)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_library -> {
                if (mViewPager.currentItem != 1) {
                    mViewPager.currentItem = 1
                    bringToTopThisTab(1)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_setting -> {
                if (mViewPager.currentItem != 2) {
                    mViewPager.currentItem = 2
                    bringToTopThisTab(2)
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    var mNavigationStack = ArrayList<Int>()
    private fun bringToTopThisTab(tabPosition: Int) {
        Tool.vibrate(context)
        mNavigationStack.remove(tabPosition)
        mNavigationStack.add(tabPosition)
    }

    override fun onBackPressed(): Boolean {
        if (mNavigationStack.isNotEmpty()) {
            if (!mNavigationAdapter!!.onBackPressed(mNavigationStack[mNavigationStack.size - 1])) {
                mNavigationStack.removeAt(mNavigationStack.size - 1)
                if (mNavigationStack.isEmpty()) return onBackPressed()
                mViewPager.setCurrentItem(mNavigationStack[mNavigationStack.size - 1], true)
            }
        } else if (mViewPager.currentItem != 0) mViewPager.setCurrentItem(0, true) else return mViewPager.currentItem != 0 || mNavigationAdapter!!.onBackPressed(0)
        return true
    }

    override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
    private var prevMenuItem: MenuItem? = null
    override fun onPageSelected(i: Int) {
        bringToTopThisTab(i)
        if (bottomNavigationView != null) {
            if (prevMenuItem != null) prevMenuItem!!.isChecked = false else bottomNavigationView!!.menu.getItem(0).isChecked = false
            bottomNavigationView!!.menu.getItem(i).isChecked = true
            prevMenuItem = bottomNavigationView!!.menu.getItem(i)
        }
    }

    override fun onPageScrollStateChanged(i: Int) {}
    override fun onServiceConnected() {}
    override fun onServiceDisconnected() {}
    override fun onQueueChanged() {}
    override fun onPlayingMetaChanged() {
        if (context != null) {
            val artist = ArtistLoader.getArtist(requireContext(), MusicPlayerRemote.getCurrentSong().artistId)
            if (backgroundImageView.visibility == View.VISIBLE) ArtistGlideRequest.Builder.from(GlideApp.with(requireContext()), artist)
                .tryToLoadOriginal(true)
                .generateBuilder(context)
                .build() /*    .error(
                                    ArtistGlideRequest
                                            .Builder
                                            .from(GlideApp.with(getContext()),mArtist)
                                            .tryToLoadOriginal(false)
                                            .generateBuilder(getContext())
                                            .build())*/
                .thumbnail(
                    ArtistGlideRequest.Builder
                        .from(GlideApp.with(requireContext()), artist)
                        .tryToLoadOriginal(false)
                        .generateBuilder(context)
                        .build()
                )
                .into(backgroundImageView)
        }
    }

    override fun onPlayStateChanged() {}
    override fun onRepeatModeChanged() {}
    override fun onShuffleModeChanged() {}
    override fun onMediaStoreChanged() {}
    override fun onPaletteChanged() {}
    override fun onDestroyView() {
        if (activity is MusicServiceActivity) (activity as MusicServiceActivity?)!!.removeMusicServiceEventListener(this)
        super.onDestroyView()
    }

    fun onUsingArtistImagePreferenceChanged() {
        mIsUsingAIAsBg = App.getInstance().preferencesUtility.isUsingArtistImageAsBackground
        if (mIsUsingAIAsBg) {
            backgroundImageView.visibility = View.VISIBLE
            onPlayingMetaChanged()
        } else {
            backgroundImageView.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "BackStackController"
    }
}