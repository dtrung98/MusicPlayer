package com.ldt.musicr.ui.floating

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.ldt.musicr.R
import com.ldt.musicr.common.AppConfig.systemBarsInset
import com.ldt.musicr.helper.extension.post
import com.ldt.musicr.notification.EventKey
import com.ldt.musicr.ui.base.FloatingViewFragment
import com.zalo.gitlabmobile.notification.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class SearchFragment: FloatingViewFragment(R.layout.screen_search_floating) {
    private lateinit var topInsetView: View
    private lateinit var searchView: View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topInsetView = view.findViewById(R.id.status_bar)
        searchView = view.findViewById(R.id.search_view)
        updateInsets()
        searchView.animate().alpha(1f).translationY(0f).setDuration(250).interpolator = FastOutSlowInInterpolator()
        view.setOnClickListener {
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
        EventKey.OnSearchInterfaceAppeared.post()
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
        EventKey.OnSearchInterfaceDisappeared.post()
    }

    private fun updateInsets() {
        topInsetView.layoutParams.height = systemBarsInset[1]
    }

    @Subscribe
    fun onEvent(event: MessageEvent) {
        if (event.key === EventKey.OnSystemBarsInsetUpdated) {
            updateInsets()
        }
    }
}