package com.ldt.musicr.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SearchConstant {
    public static final int SEARCH_FLAG_ALLOW_MATCH_CASE_CONTAIN = 1 << 3;
    public static final int SEARCH_FLAG_TO_LOW_CASE = 1 << 4;
    public static final int SEARCH_FLAG_SEARCH_NBS = 1 << 7;
    public static final int SEARCH_FLAG_PREPROCESS_KEEP_ACCENTS = 1 << 9;
    public static final int SEARCH_FLAG_PREPROCESS_TYPE_KEY = 1 << 12;
    public static final int SEARCH_FLAG_PREPROCESS_TYPE_KEY_NBS = 1 << 13;
    public static final int SEARCH_FLAG_PREPROCESS_TYPE_SEARCH = 1 << 14;
    public static final int SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND = 1 << 20; // flag used for pre-processing method only
    public static final int SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED = 1 << 21; // flag used for pre-processing method only
    public static final int SEARCH_FLAG_PREPROCESS_NO_ARRAY_WORD_OFFSETS = 1 << 24;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {
            SEARCH_FLAG_TO_LOW_CASE,
            SEARCH_FLAG_ALLOW_MATCH_CASE_CONTAIN,
            SEARCH_FLAG_SEARCH_NBS,
    })
    public @interface MatchFlag {}

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {
            SEARCH_FLAG_PREPROCESS_TYPE_KEY,
            SEARCH_FLAG_PREPROCESS_TYPE_KEY_NBS,
            SEARCH_FLAG_PREPROCESS_TYPE_SEARCH,
            SEARCH_FLAG_TO_LOW_CASE,
            SEARCH_FLAG_PREPROCESS_KEEP_ACCENTS,
            SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND,
            SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED,
            SEARCH_FLAG_PREPROCESS_NO_ARRAY_WORD_OFFSETS,
            SEARCH_FLAG_ALLOW_MATCH_CASE_CONTAIN
    })
    public @interface CommonFlag {}

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {
            SEARCH_FLAG_PREPROCESS_TYPE_KEY,
            SEARCH_FLAG_PREPROCESS_TYPE_KEY_NBS,
            SEARCH_FLAG_PREPROCESS_TYPE_SEARCH,
            SEARCH_FLAG_TO_LOW_CASE,
            SEARCH_FLAG_PREPROCESS_KEEP_ACCENTS,
            SEARCH_FLAG_PREPROCESS_SPLIT_WORD_EXTEND,
            SEARCH_FLAG_PREPROCESS_SPLIT_WORD_ENHANCED,
            SEARCH_FLAG_PREPROCESS_NO_ARRAY_WORD_OFFSETS,
    })
    public @interface PreprocessFlag {}
}
