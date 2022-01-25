package com.ldt.musicr.ui.floating

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ldt.musicr.R
import com.ldt.musicr.common.AppConfig
import com.ldt.musicr.common.AppConfig.systemBarsInset
import com.ldt.musicr.common.MediaManager
import com.ldt.musicr.helper.extension.post
import com.ldt.musicr.helper.songpreview.SongPreviewController
import com.ldt.musicr.interactors.AppExecutors
import com.ldt.musicr.interactors.postDelayedOnUiThread
import com.ldt.musicr.interactors.runOnUiThread
import com.ldt.musicr.model.Song
import com.ldt.musicr.model.item.DataItem
import com.ldt.musicr.notification.EventKey
import com.ldt.musicr.notification.MediaKey
import com.ldt.musicr.provider.ColorProvider
import com.ldt.musicr.ui.base.FloatingViewFragment
import com.ldt.musicr.ui.maintab.library.adapter.MediaAdapter
import com.ldt.musicr.ui.widget.view.MPSearchView
import com.ldt.musicr.utils.SearchUtils
import com.ldt.musicr.util.Tool
import com.ldt.musicr.utils.KeyboardUtils
import com.ldt.musicr.utils.ViewUtils
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.zalo.gitlabmobile.notification.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SearchFragment: FloatingViewFragment(R.layout.screen_search_floating) {
    private lateinit var topInsetView: View
    private lateinit var searchView: MPSearchView
    private lateinit var dimView: View

    private lateinit var recyclerView: RecyclerView

    private val adapter: MediaAdapter = MediaAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SongPreviewController.getInstance().addSongPreviewListener(adapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        SongPreviewController.getInstance().removeAudioPreviewerListener(adapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topInsetView = view.findViewById(R.id.status_bar)
        searchView = view.findViewById(R.id.search_view)
        dimView = view.findViewById(R.id.dimView)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter.layoutInflater = layoutInflater
        updateInsets()
        searchView.animate().alpha(1f).translationY(0f).setDuration(250).interpolator = FastOutSlowInInterpolator()
        postDelayedOnUiThread(250) {
            if(!isAdded || isRemoving || isDetached) return@postDelayedOnUiThread
            searchView.searchEditText.requestFocus()
            KeyboardUtils.showSoftInput(searchView.searchEditText)
        }
        setDismissOnTouchOutside()
        setHideImeOnScroll()

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        searchView.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(s?.toString() ?: "")
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setDismissOnTouchOutside() {
        if (AppConfig.isSearchInterfaceByPassTouchEvent) {
            // Consumes tap event but not moving event
            val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    dismiss()
                    return true
                }
            }
            val simpleGestureDetector = GestureDetector(requireContext(), gestureListener)
            recyclerView.setOnTouchListener { _, event -> simpleGestureDetector.onTouchEvent(event) }
        } else {
            dimView.isClickable = true
            dimView.isFocusable = true
        }
    }

    private fun setHideImeOnScroll() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var isKeyboardDismissedByScroll = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, state: Int) {
                when (state) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> if (!isKeyboardDismissedByScroll) {
                        KeyboardUtils.hideSoftInput(searchView.searchEditText)
                        isKeyboardDismissedByScroll = !isKeyboardDismissedByScroll
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        isKeyboardDismissedByScroll = false
                    }
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
        EventKey.OnSearchInterfaceAppeared.post()
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
        KeyboardUtils.hideSoftInput(activity)
        EventKey.OnSearchInterfaceDisappeared.post()
    }

    private val dpX52 by lazy { ViewUtils.convertDipsToPixels (0f /* + 32f + 20 + 45*/)}
    private val dpX20 by lazy { ViewUtils.convertDipsToPixels(0f) }

    private fun updateInsets() {
        topInsetView.layoutParams.height = systemBarsInset[1]
        recyclerView.setPadding(dpX20 + systemBarsInset[0], dpX52 + systemBarsInset[1], dpX20 + systemBarsInset[2], systemBarsInset[3])
    }

    @Subscribe
    fun onEvent(event: MessageEvent) {
        when (event.key) {
            EventKey.OnSystemBarsInsetUpdated -> updateInsets()
            EventKey.OnPaletteChanged -> {
                if (recyclerView is FastScrollRecyclerView) {
                    val recyclerView = recyclerView as FastScrollRecyclerView
                    recyclerView.setPopupBgColor(Tool.getHeavyColor())
                    recyclerView.setThumbColor(Tool.getHeavyColor())
                }
                adapter.onThemeChanged()
                //adapter.notifyOnMediaStateChanged(AbsMediaAdapter.PALETTE_CHANGED)
            }
            EventKey.OnPlayStateChanged -> {
                //adapter.notifyOnMediaStateChanged(AbsMediaAdapter.PLAY_STATE_CHANGED)
                adapter.onPlayingStateChanged()
            } else -> {}
        }
    }

    private fun search(text: String) {
        AppExecutors.io().execute {
            val keyword = text.replace("\n\r", " ").replace('\n', ' ').replace('\r', ' ').trim()

            val result = mutableListOf<DataItem>()

            if(keyword.isBlank()) {
                if(AppConfig.isShowEmptyViewInLibrarySongTab) {
                    result.add(DataItem.Empty.NoResult)
                }
                submitList(result)
                return@execute
            }

            if(!MediaManager.isLoadedSongs) {
                // fetch later
                return@execute
            }

            val playlist = MediaManager.getPlaylist(MediaKey.PLAYLIST_ID_ALL_SONGS) ?: run {
                if(AppConfig.isShowEmptyViewInLibrarySongTab) {
                    result.add(DataItem.Empty.Error)
                }
                submitList(result)
                return@execute
            }

            val songs = mutableListOf<DataItem.SongItem>()
            val srcTopHitSongs = mutableListOf<Song>()

            playlist.songs.forEach { songId ->
                MediaManager.getSong(songId)?.also { song ->
                    srcTopHitSongs.add(song)
                }
            }

            val desTopHitSongs = mutableListOf<Song>()
            SearchUtils.filterTopHitEntities(srcTopHitSongs, desTopHitSongs, keyword, 0)
            desTopHitSongs.sortByDescending { it.searchScore }

            desTopHitSongs.forEach { song ->
                songs.add(DataItem.SongItem(DataItem.FLAG_DIM, song, songs.size, playlist.id, name = ViewUtils.getHighlightedText(song.title, song.spanPosList, ColorProvider.baseColorL45, ViewUtils.MEDIUM)))
            }

            result.addAll(songs)

            // Add Sorting Tile
            if(AppConfig.isShowSortingTileInLibrarySongTab && songs.isNotEmpty()) {
                result.add(0, DataItem.SortingTile)
            }

            if(result.isNotEmpty()) {
                result.add(0, DataItem.Dim.TopGradientDim)
                result.add(DataItem.Dim.BottomGradientDim)
            }

            submitList(result)
        }
    }

    private fun submitList(data: List<DataItem>) {
        runOnUiThread {
            if(isAdded && !isRemoving) {
                adapter.submitList(data)
            }
        }
    }
}