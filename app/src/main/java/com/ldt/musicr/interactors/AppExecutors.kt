package com.ldt.musicr.interactors

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
object AppExecutors {
    private val singleThreadExecutor: Executor by lazy { Executors.newSingleThreadExecutor() }
    private val defaultExecutor: Executor by lazy { Executors.newFixedThreadPool(24) }

    @JvmStatic
    fun single(): Executor {
        return singleThreadExecutor
    }

    @JvmStatic
    fun io(): Executor {
        return defaultExecutor
    }
}