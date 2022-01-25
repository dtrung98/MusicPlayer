package com.ldt.musicr.utils

import com.ldt.musicr.model.core.Entity
import java.util.*

/**
 * Utils class handles search functionality
 *
 * @author github.com/dtrung98
 */
object SearchUtils {
    private const val splitWordExtendTypeKeyEnabled = false
    private const val splitWordEnhancedTypeKeyEnabled = true
    private const val splitWordExtendTypeSearchEnabled = false
    private const val splitWordEnhancedTypeSearchEnabled = true

    private const val percentMatchingScoreEnabled: Boolean = false
    private const val fullMatchCommonBonusEnabled: Boolean = false

    private const val matchAccentBonusValue = 4000
    private const val matchAccentBonusEnabled = true

    /**
     * Note: desList must be an empty list or you will need to check dup final desList
     */
    @JvmStatic
    fun <T> filterTopHitEntities(srcList: List<T>, desList: MutableList<T>, keyword: String, @SearchConstant.MatchFlag flags: Int = 0) where T : Entity {
        srcList.forEach {
            val matchScore = retrieveMatchScore(keyword, it.displayName, null, null, it, it.spanPosList, flags)
            it.searchScore = matchScore
            if(matchScore > 0) {
                desList.add(it)
            }
        }
    }

    fun retrieveTopHitScore(): Float {
        return 0f
    }

    private fun hasFlags(flags: Int, flagsNeedToCheck: Int): Boolean {
        return Utils.hasFlags(flags, flagsNeedToCheck)
    }

    @JvmStatic
    private fun retrieveMatchScore(
        key: String?,
        search: String?,
        paramArrayKeyWord: Array<String>?,
        paramArrayItemWord: Array<String>?,
        entity: Entity,
        spanPosArray: MutableList<Int>,
        @SearchConstant.MatchFlag flags: Int = 0
    ): Float {
        var matchScore = 0f

        val arrayItemWordLowercase: Array<String>
        var arrayItemWordOffsets: IntArray? = null

        /* the initial assigned value of fullMatchAccentsBonusChecking */
        val fullMatchAccentsBonusEnabled: Boolean

        /* Check if we need to check full match accents bonus case, this variables can change between steps */
        var fullMatchAccentsBonusChecking = matchAccentBonusEnabled
        fullMatchAccentsBonusEnabled = fullMatchAccentsBonusChecking
        val lowerDpn = ""
        val arrListPostStartEndMatch: MutableMap<Int, ArrayList<Int>?> = HashMap()

        val arrayItemWordKeepAccents: Array<String>
        val arrayItemWordOrg: Array<String> = when {
            paramArrayItemWord != null -> {
                /* Deprecated: pass a ready search word array */
                if (hasFlags(flags, SearchConstant.SEARCH_FLAG_TO_LOW_CASE)) {
                    arrayItemWordLowercase = paramArrayItemWord
                } else {
                    arrayItemWordLowercase = Array(paramArrayItemWord.size) { paramArrayItemWord[it].lowercase() }
                    for (i in paramArrayItemWord.indices) {
                        arrayItemWordLowercase[i] = paramArrayItemWord[i].lowercase()
                    }
                }
                fullMatchAccentsBonusChecking = false
                arrayItemWordKeepAccents = emptyArray()
                paramArrayItemWord
            }
            search != null -> {
                // ThanhMinh -> Thanh, Minh (if flag toLowerCase is unset)
                val searchOutputOrg = SearchPreProcessingProvider.get(
                    search,
                    SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_SEARCH or (if (hasFlags(
                            flags,
                            SearchConstant.SEARCH_FLAG_TO_LOW_CASE
                        )
                    ) SearchConstant.SEARCH_FLAG_TO_LOW_CASE else 0) // this flag should always be unset
                            or (if (splitWordExtendTypeSearchEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND else 0)
                            or if (splitWordEnhancedTypeSearchEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED else 0
                )
                if (searchOutputOrg is PreProcessingOffsetOutput) {
                    arrayItemWordOffsets = searchOutputOrg.arrayWordOffsets
                }

                // ThanhMinh -> thanh, minh
                arrayItemWordLowercase =
                    if (hasFlags(flags, SearchConstant.SEARCH_FLAG_TO_LOW_CASE)) searchOutputOrg.arrayWords else SearchPreProcessingProvider.get(
                        search,
                        SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_SEARCH or SearchConstant.SEARCH_FLAG_TO_LOW_CASE
                                or (if (splitWordExtendTypeSearchEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND else 0)
                                or if (splitWordEnhancedTypeSearchEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED else 0
                    ).arrayWords
                arrayItemWordKeepAccents = SearchPreProcessingProvider.get(
                    search,
                    (SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_SEARCH or SearchConstant.SEARCH_FLAG_TO_LOW_CASE
                            or (if (splitWordExtendTypeSearchEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND else 0)
                            or (if (splitWordEnhancedTypeSearchEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED else 0)
                            or SearchConstant.SEARCH_FLAG_PREPROCESS_KEEP_ACCENTS)
                ).arrayWords
                if (arrayItemWordKeepAccents.size != arrayItemWordLowercase.size) {
                    // something wrong, two arrays must have the same length
                    fullMatchAccentsBonusChecking = false
                }
                searchOutputOrg.arrayWords
            }
            else -> {
                // no way to retrieve the search word array, so return
                return matchScore
            }
        }
        val arrayKeyWordKeepAccents: Array<String>
        val arrayKeyWord: Array<String> = when {
            paramArrayKeyWord != null -> {
                /* Deprecated: pass a ready search word array */
                fullMatchAccentsBonusChecking = false
                arrayKeyWordKeepAccents = emptyArray()
                paramArrayKeyWord
            }
            key != null -> {
                // keywords will be transform to lowercase by default
                val arrayKeyWord = if (hasFlags(flags, SearchConstant.SEARCH_FLAG_SEARCH_NBS)) SearchPreProcessingProvider.get(
                    key,
                    (SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_KEY_NBS or SearchConstant.SEARCH_FLAG_TO_LOW_CASE
                            or (if (splitWordExtendTypeKeyEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND else 0)
                            or (if (splitWordEnhancedTypeKeyEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED else 0))
                ).arrayWords else SearchPreProcessingProvider.get(
                    key,
                    (SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_KEY or SearchConstant.SEARCH_FLAG_TO_LOW_CASE
                            or (if (splitWordExtendTypeKeyEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND else 0)
                            or (if (splitWordEnhancedTypeKeyEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED else 0))
                ).arrayWords
                arrayKeyWordKeepAccents = if (hasFlags(flags, SearchConstant.SEARCH_FLAG_SEARCH_NBS)) SearchPreProcessingProvider.get(
                    key, (SearchConstant.SEARCH_FLAG_PREPROCESS_TYPE_KEY_NBS or SearchConstant.SEARCH_FLAG_TO_LOW_CASE
                            or (if (splitWordExtendTypeKeyEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND else 0)
                            or (if (splitWordEnhancedTypeKeyEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED else 0)
                            or SearchConstant.SEARCH_FLAG_PREPROCESS_KEEP_ACCENTS)
                ).arrayWords else SearchPreProcessingProvider.get(
                    key,
                    (SearchConstant.SEARCH_FLAG_PREPROCESS_KEEP_ACCENTS or SearchConstant.SEARCH_FLAG_TO_LOW_CASE
                            or (if (splitWordExtendTypeKeyEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND else 0)
                            or (if (splitWordEnhancedTypeKeyEnabled) SearchConstant.SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED else 0)
                            or SearchConstant.SEARCH_FLAG_PREPROCESS_KEEP_ACCENTS)
                ).arrayWords
                if (arrayKeyWordKeepAccents.size != arrayKeyWord.size) {
                    // something wrong, two arrays must have the same size
                    fullMatchAccentsBonusChecking = false
                }

                // the old way
                // arrKey = hasFlags(flags, SEARCH_FLAG_VALID_SEARCH_USERNAME) ? preprocessKeyStrForUserName(key) : preprocessKeyStr(key);
                arrayKeyWord
            }
            else -> {
                // no way to retrieve the array key words, so return
                return matchScore
            }
        }
        val sizeDpnElement = arrayItemWordLowercase.size
        val dpnAlreadyMatch = IntArray(sizeDpnElement)
        Arrays.fill(dpnAlreadyMatch, -2)
        var matchCount = 0

        spanPosArray.clear()
        var index: Int
        var indexBestScoreLength = 0
        if (arrayKeyWord.size > 1) {
            arrayKeyWord.sortedBy { it.length }
        }
        if (!arrayKeyWordKeepAccents.isNullOrEmpty()) {
            arrayKeyWordKeepAccents.sortBy { it.length }
        }
        var hasContainFullMatch = false
        var hasStartWithMatch = false
        var hasContainAccentsInKey = false
        var numberFullMatchKeepAccents = 0 // number full match in accent words between search words array and key words array
        for (i in arrayKeyWord.indices)  //for each work in key word string - example: One Two Three
        {
            var preCalStartPos = 0
            var startPosSearch = 0
            var bestPos = -1
            var bestPosLength = 0
            var findTheBestMatch = false
            var startMatchPos = -1
            var findTheBestMatchCaseKeepAccents = false

            // cached values for full_match_common case
            var bestHighPosCaseFullMatchCommon = bestPos
            var startPosSearchCaseFullMatchCommon = startPosSearch
            var findTheBestMatchCaseFullMatchCommon = findTheBestMatch
            var startMatchPosCaseFullMatchCommon = startMatchPos
            for (t in 0 until sizeDpnElement)  // for each work in Display name string - example: Peter One.
            {
                index = -1
                if (t == 0) findTheBestMatch = false
                indexBestScoreLength += arrayItemWordLowercase[t].length
                if (t == 0) preCalStartPos = 0 else preCalStartPos += (arrayItemWordLowercase[t - 1].length + 1) //plus 1 for space
                if (dpnAlreadyMatch[t] == -1) continue  //ignore if match before
                var countLoopToFind = 0
                index = arrayItemWordLowercase[t].indexOf((arrayKeyWord[i]) /*dpnAlreadyMatch[t]*/)
                while (index >= 0 && countLoopToFind <= 10) {
                    if (index >= 0) {
                        var match = false
                        if (hasFlags(flags, SearchConstant.SEARCH_FLAG_ALLOW_MATCH_CASE_CONTAIN)) {
                            // no need to match start-with
                            match = true
                        } else if (index == 0) {
                            match = true
                        } else {
                            // Check if matching word in PascalCase convention, like "Minh" of "LeMinh"
                            val c1 = arrayItemWordOrg[t][index]
                            val c2 = arrayItemWordOrg[t][index - 1]
                            match = (Character.isUpperCase(c1) && (Character.isLowerCase(c2) || (c2 > 'z') || (c2 < 'A')))
                        }
                        if (match) {
                            try {
                                if (arrListPostStartEndMatch[t] != null && arrListPostStartEndMatch[t]!!.size > 0) {
                                    var needToBreak = false
                                    var l = 0
                                    while (l < arrListPostStartEndMatch[t]!!.size - 1) {
                                        if (index >= arrListPostStartEndMatch[t]!![l] && index < arrListPostStartEndMatch[t]!![l + 1]) {
                                            needToBreak = true
                                            countLoopToFind++
                                            index = arrayItemWordLowercase[t].indexOf((arrayKeyWord[i]), index + (arrayKeyWord[i].length) /*dpnAlreadyMatch[t]*/)
                                            break
                                        }
                                        l += 2
                                    }
                                    if (needToBreak) continue
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (fullMatchAccentsBonusEnabled && (arrayItemWordKeepAccents!![t] == arrayKeyWordKeepAccents[i])) {
                                // full_match_accented detected
                                bestPos = t
                                startPosSearch = preCalStartPos
                                findTheBestMatch = true
                                startMatchPos = index
                                findTheBestMatchCaseKeepAccents = true

                                // go out of the while-loop
                                // here we found the best match position
                                break
                            }
                            if (!findTheBestMatch && !findTheBestMatchCaseFullMatchCommon && (arrayItemWordLowercase[t] == arrayKeyWord[i])) {
                                // full_match_common detected (first time)
                                if (!fullMatchAccentsBonusChecking) {
                                    // go out of the while-loop
                                    // here we fount the best match position
                                    bestPos = t
                                    startPosSearch = preCalStartPos
                                    findTheBestMatch = true
                                    startMatchPos = index
                                    break // out of while-loop to the next DPN element  & find the best break of the for to the next keyword element
                                }

                                // keep looping
                                // until we found a full_match_accented case
                                // btw, save the first match position for restoration in case it doesn't exist
                                bestHighPosCaseFullMatchCommon = t
                                startPosSearchCaseFullMatchCommon = preCalStartPos
                                findTheBestMatchCaseFullMatchCommon = true
                                startMatchPosCaseFullMatchCommon = index
                            }
                            if (!findTheBestMatchCaseFullMatchCommon && !findTheBestMatch && (bestPos == -1 || (arrayItemWordLowercase[t].length < bestPosLength))) {
                                bestPos = t
                                bestPosLength = arrayItemWordLowercase[t].length
                                startPosSearch = preCalStartPos
                                startMatchPos = index
                                break // out of while to the next DPN element
                            }
                            countLoopToFind++
                            index = arrayItemWordLowercase[t].indexOf((arrayKeyWord[i]), index + (arrayKeyWord[i].length) /*dpnAlreadyMatch[t]*/)
                        } else {
                            countLoopToFind++
                            index = arrayItemWordLowercase[t].indexOf((arrayKeyWord[i]), index + (arrayKeyWord[i].length) /*dpnAlreadyMatch[t]*/)
                        }
                    } else {
                        countLoopToFind++
                    }
                }
                if (findTheBestMatch) break
            }

            // restore the best match case if it is matched by full_match_common case
            if (!findTheBestMatch && findTheBestMatchCaseFullMatchCommon) {
                bestPos = bestHighPosCaseFullMatchCommon
                startPosSearch = startPosSearchCaseFullMatchCommon
                findTheBestMatch = findTheBestMatchCaseFullMatchCommon
                startMatchPos = startMatchPosCaseFullMatchCommon
                index = startMatchPosCaseFullMatchCommon
            }

            //calculate point with best pos
            try {
                if (bestPos >= 0) {
                    val percent = (arrayKeyWord[i].length.toFloat() / arrayItemWordLowercase[bestPos].length) //for compare percent special word
                    var indexStartMatch = 0
                    var indexEndMatch = 0
                    if (arrayKeyWord[i].length != arrayItemWordLowercase[bestPos].length) {
                        indexStartMatch = arrayItemWordLowercase[bestPos].indexOf((arrayKeyWord[i]), startMatchPos)
                        indexEndMatch = indexStartMatch + arrayKeyWord[i].length
                        if (arrListPostStartEndMatch.containsKey(bestPos) && arrListPostStartEndMatch[bestPos] != null) {
                            arrListPostStartEndMatch[bestPos]!!.add(indexStartMatch)
                            arrListPostStartEndMatch[bestPos]!!.add(indexEndMatch)
                        } else arrListPostStartEndMatch[bestPos] = ArrayList(Arrays.asList(indexStartMatch, indexEndMatch))
                    }
                    if (percent == 1f) {
                        hasContainFullMatch = true
                    } else {
                        index = arrayItemWordLowercase[bestPos].indexOf((arrayKeyWord[i]))
                        if (index == 0 && !hasStartWithMatch) {
                            hasStartWithMatch = true
                        }
                    }
                    val fullMatchKeepAccentsCase = findTheBestMatchCaseKeepAccents

                    // Check full_match_accents
                    if (fullMatchAccentsBonusChecking && (percent == 1f) && fullMatchKeepAccentsCase) {
                        numberFullMatchKeepAccents++
                        if (!hasContainAccentsInKey) {
                            hasContainAccentsInKey = arrayKeyWord[i] == arrayKeyWordKeepAccents[i]
                        }
                    } else {
                        // require every words in key are matched by full_match_accents
                        // so, disable full_match accents check here
                        fullMatchAccentsBonusChecking = false
                    }
                    if (percentMatchingScoreEnabled) {
                        // increase by percent matching score
                        matchScore += (percent * 100)
                    } else {
                        // add a fixed score
                        matchScore += 1f
                    }
                    matchCount++
                    val startPos: Int
                    val endPos: Int
                    if (arrayItemWordOffsets == null) {
                        val lenghtToFind = 0
                        var currentPosToFind = -1
                        for (l in 0 until bestPos) {
                            currentPosToFind += arrayItemWordLowercase[l].length
                            currentPosToFind++
                        }
                        if (bestPos == 0) currentPosToFind = 0
                        startPos = lowerDpn.indexOf((arrayKeyWord[i]), if (currentPosToFind >= 0) (currentPosToFind + startMatchPos) else 0)
                        endPos = startPos + arrayKeyWord[i].length
                    } else {
                        /* use saved array offsets */
                        startPos = arrayItemWordOffsets[bestPos] + startMatchPos
                        endPos = startPos + arrayKeyWord[i].length
                    }
                    if (arrayKeyWord[i].length == arrayItemWordLowercase[bestPos].length) dpnAlreadyMatch[bestPos] = -1
                    if (startPos != -1) {
                        spanPosArray.add(startPos)
                        spanPosArray.add(endPos)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (matchCount == 0 || matchCount != arrayKeyWord.size) {
            matchScore = 0f
        } else if (arrayKeyWord.size > 0) {
            if (matchCount > 0) {
                matchScore /= matchCount

//                    if (hasContainFullMatch || (arrayItemWordLowercase.size == 1 && hasStartWithMatch)) {
//                        if (fullMatchCommonBonusEnabled) {
//                            // add bonus score for full match cases
//                        }
//                        // Case: Match full
//                    } else if (hasStartWithMatch) {
//                        if (fullMatchCommonBonusEnabled) {
//                            // add smaller bonus score for start_with match cases
//
//                        }
//                        // Case: Match start with
//                    }


                // match full keep accents and at least one word is accented
                val hasContainAccentsAndFullMatchKeepAccents = fullMatchAccentsBonusChecking && hasContainAccentsInKey && (numberFullMatchKeepAccents == arrayKeyWord.size)
                if (hasContainAccentsAndFullMatchKeepAccents) {
                    matchScore += matchAccentBonusValue
                }
            }
        }
        return matchScore
    }

    @JvmStatic
    private fun filterTopHitSample() {
        val srcList = mutableListOf<Entity>()
        val desList = mutableListOf<Entity>()
        srcList.forEach {
            val matchScore = retrieveMatchScore(null, null, emptyArray(), emptyArray(), it, it.spanPosList, SearchConstant.SEARCH_FLAG_TO_LOW_CASE)
        }
    }

}