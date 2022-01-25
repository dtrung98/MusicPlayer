package com.ldt.musicr.utils

import java.util.concurrent.ConcurrentHashMap

/**
 * Create and cache pre-process key/search string used in search logic
 */
object SearchPreProcessingProvider {
    private val maps = ConcurrentHashMap<Int, ConcurrentHashMap<String, PreProcessingOutput>>()

    @JvmStatic
    fun get(input: String, @SearchConstant.PreprocessFlag flags: Int): PreProcessingOutput {
        val map = maps[flags]
                ?: ConcurrentHashMap<String, PreProcessingOutput>().apply { maps[flags] = this }
        return map[input] ?: create(input, flags).apply { map[input] = this }
    }

    @JvmStatic
    private fun create(input: String, @SearchConstant.PreprocessFlag flags: Int): PreProcessingOutput {
        return when {
            flags.hasFlags(SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_KEY) -> SearchPreProcessingUtils.preprocessTypeKey(input, flags)
            flags.hasFlags(SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_KEY_NBS) -> SearchPreProcessingUtils.preprocessTypeKeyUsername(input, flags)
            else /*SearchConstant.hasFlags(flags, SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_SEARCH)*/ -> SearchPreProcessingUtils.preprocessTypeSearch(input, flags)
        }
    }

    /**
     * Clear all saved value
     */
    @JvmStatic
    fun clear() {
        maps.clear()
    }

    /**
     * Clear all saved values by its flags.
     */
    @JvmStatic
    fun clearFlags(flags: Int) {
        maps.remove(flags)
    }

    private fun Int.hasFlags(flagsNeedToCheck: Int): Boolean {
        return (this and flagsNeedToCheck) != 0
    }

}

open class PreProcessingOutput(val arrayWords: Array<String>)
open class PreProcessingOffsetOutput(arrayWords: Array<String>, val arrayWordOffsets: IntArray): PreProcessingOutput(arrayWords)