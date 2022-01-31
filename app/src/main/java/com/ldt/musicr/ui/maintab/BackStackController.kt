package com.ldt.musicr.ui.maintab

import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.cardview.widget.CardView
import com.ldt.musicr.ui.widget.viewpager.GestureControlViewPager
import com.ldt.musicr.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ldt.musicr.ui.CardLayerController.CardLayerAttribute
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ldt.musicr.ui.AppActivity
import com.ldt.musicr.ui.maintab.library.LibraryTabFragment
import com.ldt.musicr.ui.widget.navigate.BackPressableFragment
import com.ldt.musicr.service.MusicPlayerRemote
import com.ldt.musicr.glide.ArtistGlideRequest
import com.ldt.musicr.glide.GlideApp
import com.ldt.musicr.App
import com.ldt.musicr.common.MediaManager
import com.ldt.musicr.interactors.AppExecutors
import com.ldt.musicr.interactors.postOnUiThread
import com.ldt.musicr.notification.EventKey
import com.ldt.musicr.util.Tool
import com.zalo.gitlabmobile.notification.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class BackStackController : CardLayerFragment(), OnPageChangeListener {
    private var mRoot: CardView? = null
    private lateinit var mDimView: View
    private lateinit var backgroundImageView: ImageView
    private lateinit var mViewPager: GestureControlViewPager
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
        dpX1 = resources.getDimension(R.dimen.oneDP)
        mNavigationHeight = view.resources.getDimension(R.dimen.bottom_navigation_height)
        mNavigationAdapter = BottomNavigationPagerAdapter(activity, childFragmentManager)
        mViewPager.adapter = mNavigationAdapter
        mViewPager.offscreenPageLimit = 3
        mViewPager.isSwipeGestureEnabled = false
        mViewPager.setOnTouchListener { _: View?, event: MotionEvent? -> mCardLayerController.dispatchOnTouchEvent(mRoot, event) }
        updateBackImage()
    }

    private fun bindView(view: View) {
        mRoot = view.findViewById(R.id.root)
        mDimView = view.findViewById(R.id.dim_view)
        backgroundImageView = view.findViewById(R.id.back_image)
        mViewPager = view.findViewById(R.id.view_pager)
    }

    fun dispatchOnTouchEvent(event: MotionEvent?): Boolean {
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
            val distance = max - min
            val postFactor = (me - 1.0f) / (me - 0.75f) // 1/2, 2/3,3/4, 4/5, 5/6 ...
            val preFactor = (me - 2.0f) / (me - 0.75f) // 0/1, 1/2, 2/3, ...
            var darken = min + distance * preFactor + distance * (postFactor - preFactor) * attrs[actives[0]].runtimePercent
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
        bottomNavigationParent?.also {
            val bnpHeight = it.height
            if (me == 1) {
                var pc = attrs[actives[0]].runtimePercent
                if (pc > 1) pc = 1f else if (pc < 0) pc = 0f
                it.translationY = pc * bnpHeight
            } else if (me != 0) {
                it.translationY = bnpHeight.toFloat()
            }
        }
    }

    override fun onLayerHeightChanged(attr: CardLayerAttribute) {
        mRoot?.also {
            var pc = attr.mCurrentTranslate / attr.maxPosition
            if (pc > 1) pc = 1f else if (pc < 0) pc = 0f
            val scale = 0.2f + pc * 0.8f
            var radius = 1 - pc
            if (radius < 0.1f) radius = 0f else if (radius > 1) radius = 1f
            it.radius = dpX1 * 14 * radius
            it.alpha = scale
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
        val fragment = navigateToTab(go)
        return if (fragment is LibraryTabFragment) {
            fragment
        } else null
    }

    private fun navigateToTab(go: Boolean, item: Int = 1): Fragment? {
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
    private var navigationStack = ArrayList<Int>()
    private fun bringToTopThisTab(tabPosition: Int) {
        //Tool.vibrate(context)
        view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        navigationStack.remove(tabPosition)
        navigationStack.add(tabPosition)
    }

    override fun onBackPressed(): Boolean {
        if (navigationStack.isNotEmpty()) {
            if (!mNavigationAdapter!!.onBackPressed(navigationStack[navigationStack.size - 1])) {
                navigationStack.removeAt(navigationStack.size - 1)
                if (navigationStack.isEmpty()) return onBackPressed()
                mViewPager.setCurrentItem(navigationStack[navigationStack.size - 1], true)
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

    private fun updateBackImage() {
        AppExecutors.io().execute {
            val isUseArtistImageAsBackground = App.getInstance().preferencesUtility.isUsingArtistImageAsBackground
            val currentSong = if(isUseArtistImageAsBackground) MusicPlayerRemote.getCurrentSong() else null
            val artist = currentSong?.let { MediaManager.getArtist(it.artistId) }
            postOnUiThread {
                if(isAdded && !isRemoving) {
                    backgroundImageView.visibility = if(isUseArtistImageAsBackground) View.VISIBLE else View.GONE
                    artist ?: return@postOnUiThread
                    ArtistGlideRequest.Builder.from(GlideApp.with(requireContext()), artist)
                        .requestHighResolutionArt(true)
                        .generateBuilder(context)
                        .build()
                        .thumbnail(
                            ArtistGlideRequest.Builder
                                .from(GlideApp.with(requireContext()), artist)
                                .requestHighResolutionArt(false)
                                .generateBuilder(context)
                                .build()
                        )
                        .into(backgroundImageView)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onEvent(messageEvent: MessageEvent) {
        when (messageEvent.key) {
            EventKey.OnLoadedArtists, EventKey.OnPlayingMetaChanged, EventKey.SettingKey.ChangedSetArtistArtworkAsBackground -> {
                updateBackImage()
            }
            else -> {}
        }
    }

    companion object {
        private const val TAG = "BackStackController"
    }
}