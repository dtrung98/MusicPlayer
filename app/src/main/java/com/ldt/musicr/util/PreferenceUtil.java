package com.ldt.musicr.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.ldt.musicr.App;
import com.ldt.musicr.R;


import java.io.File;

public final class PreferenceUtil {
    private static final String SONG_CHILD_SORT_ORDER = "song_child_sort_order";
    public static final String GENERAL_THEME = "general_theme";
    public static final String REMEMBER_LAST_TAB = "remember_last_tab";
    public static final String LAST_PAGE = "last_start_page";
    public static final String LAST_MUSIC_CHOOSER = "last_music_chooser";
    public static final String NOW_PLAYING_SCREEN_ID = "now_playing_screen_id";

    public static final String ARTIST_SORT_ORDER = "artist_sort_order";
    public static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";
    public static final String ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order";
    public static final String ALBUM_SORT_ORDER = "album_sort_order";
    public static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";
    public static final String SONG_SORT_ORDER = "song_sort_order";
    public static final String GENRE_SORT_ORDER = "genre_sort_order";

    public static final String ALBUM_GRID_SIZE = "album_grid_size";
    public static final String ALBUM_GRID_SIZE_LAND = "album_grid_size_land";

    public static final String SONG_GRID_SIZE = "song_grid_size";
    public static final String SONG_GRID_SIZE_LAND = "song_grid_size_land";

    public static final String ARTIST_GRID_SIZE = "artist_grid_size";
    public static final String ARTIST_GRID_SIZE_LAND = "artist_grid_size_land";

    public static final String ALBUM_COLORED_FOOTERS = "album_colored_footers";
    public static final String SONG_COLORED_FOOTERS = "song_colored_footers";
    public static final String ARTIST_COLORED_FOOTERS = "artist_colored_footers";
    public static final String ALBUM_ARTIST_COLORED_FOOTERS = "album_artist_colored_footers";

    public static final String FORCE_SQUARE_ALBUM_COVER = "force_square_album_art";

    public static final String COLORED_NOTIFICATION = "colored_notification";
    public static final String CLASSIC_NOTIFICATION = "classic_notification";

    public static final String COLORED_APP_SHORTCUTS = "colored_app_shortcuts";

    public static final String AUDIO_DUCKING = "audio_ducking";
    public static final String GAPLESS_PLAYBACK = "gapless_playback";

    public static final String LAST_ADDED_CUTOFF = "last_added_interval";
    public static final String RECENTLY_PLAYED_CUTOFF = "recently_played_interval";

    public static final String ALBUM_ART_ON_LOCKSCREEN = "album_art_on_lockscreen";
    public static final String BLURRED_ALBUM_ART = "blurred_album_art";

    public static final String LAST_SLEEP_TIMER_VALUE = "last_sleep_timer_value";
    public static final String NEXT_SLEEP_TIMER_ELAPSED_REALTIME = "next_sleep_timer_elapsed_real_time";
    public static final String SLEEP_TIMER_FINISH_SONG = "sleep_timer_finish_music";

    public static final String IGNORE_MEDIA_STORE_ARTWORK = "ignore_media_store_artwork";

    public static final String LAST_CHANGELOG_VERSION = "last_changelog_version";
    public static final String INTRO_SHOWN = "intro_shown";

    public static final String AUTO_DOWNLOAD_IMAGES_POLICY = "auto_download_images_policy";

    public static final String START_DIRECTORY = "start_directory";

    public static final String SYNCHRONIZED_LYRICS_SHOW = "synchronized_lyrics_show";

    public static final String INITIALIZED_BLACKLIST = "initialized_blacklist";

    public static final String LIBRARY_CATEGORIES = "library_categories";

    private static final String REMEMBER_SHUFFLE = "remember_shuffle";

    private static final String USE_ARTIST_IMAGE_AS_BACKGROUND = "use_artist_image_as_bg";
    public static final String IN_APP_VOLUME = "in_app_volume";
    private static final String AUDIO_MIN_DURATION = "audio_min_duration";
    public static final String BALANCE_VALUE = "balance_value";
    public static final String THREAD_NUMBER = "thread_number";

    private static PreferenceUtil sInstance;

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    private boolean isFirstTime = true;

    private final SharedPreferences mPreferences;

    private PreferenceUtil(@NonNull final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceUtil getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(App.getInstance().getApplicationContext());
        }
        return sInstance;
    }
    public static PreferenceUtil getInstance() {
        if (sInstance == null) {
            sInstance = new PreferenceUtil(App.getInstance().getApplicationContext());
        }
        return sInstance;
    }

    public static boolean isAllowedToDownloadMetadata(final Context context) {
        switch (getInstance(context).autoDownloadImagesPolicy()) {
            case "always":
                return true;
            case "only_wifi":
                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                return netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnectedOrConnecting();
            case "never":
            default:
                return false;
        }
    }

    public void registerOnSharedPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        mPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public void unregisterOnSharedPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        mPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }


    public void setGeneralTheme(String theme) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(GENERAL_THEME, theme);
        editor.commit();
    }


    public final boolean rememberLastTab() {
        return mPreferences.getBoolean(REMEMBER_LAST_TAB, true);
    }

    public void setLastPage(final int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LAST_PAGE, value);
        editor.apply();
    }

    public final int getLastPage() {
        return mPreferences.getInt(LAST_PAGE, 0);
    }

    public void setLastMusicChooser(final int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LAST_MUSIC_CHOOSER, value);
        editor.apply();
    }

    public final int getLastMusicChooser() {
        return mPreferences.getInt(LAST_MUSIC_CHOOSER, 0);
    }



    public final boolean coloredNotification() {
        return mPreferences.getBoolean(COLORED_NOTIFICATION, true);
    }

    public final boolean classicNotification() {
        return mPreferences.getBoolean(CLASSIC_NOTIFICATION, false);
    }

    public void setColoredNotification(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(COLORED_NOTIFICATION, value);
        editor.apply();
    }

    public void setClassicNotification(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(CLASSIC_NOTIFICATION, value);
        editor.apply();
    }

    public void setColoredAppShortcuts(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(COLORED_APP_SHORTCUTS, value);
        editor.apply();
    }

    public final boolean coloredAppShortcuts() {
        return mPreferences.getBoolean(COLORED_APP_SHORTCUTS, true);
    }

    public final boolean gaplessPlayback() {
        return mPreferences.getBoolean(GAPLESS_PLAYBACK, false);
    }

    public final boolean audioDucking() {
        return mPreferences.getBoolean(AUDIO_DUCKING, true);
    }

    public final boolean albumArtOnLockscreen() {
        return mPreferences.getBoolean(ALBUM_ART_ON_LOCKSCREEN, true);
    }

    public final boolean blurredAlbumArt() {
        return mPreferences.getBoolean(BLURRED_ALBUM_ART, false);
    }

    public final boolean ignoreMediaStoreArtwork() {
        return mPreferences.getBoolean(IGNORE_MEDIA_STORE_ARTWORK, false);
    }

    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(final String sortOrder) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(ARTIST_SORT_ORDER, sortOrder);
        editor.commit();
    }

    public final String getArtistSongSortOrder() {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER, SortOrder.ArtistSongSortOrder.SONG_A_Z);
    }

    public final String getArtistAlbumSortOrder() {
        return mPreferences.getString(ARTIST_ALBUM_SORT_ORDER, SortOrder.ArtistAlbumSortOrder.ALBUM_YEAR);
    }

    public final String getAlbumSortOrder() {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(final String sortOrder) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(ALBUM_SORT_ORDER, sortOrder);
        editor.commit();
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER, SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(final String sortOrder) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SONG_SORT_ORDER, sortOrder);
        editor.commit();
    }

    public final String getGenreSortOrder() {
        return mPreferences.getString(GENRE_SORT_ORDER, SortOrder.GenreSortOrder.GENRE_A_Z);
    }

    // The last added cutoff time is compared against the Android media store timestamps, which is seconds based.
    public long getLastAddedCutoffTimeSecs() {
        return getCutoffTimeMillis(LAST_ADDED_CUTOFF) / 1000;
    }

    // The recently played cutoff time is compared against the internal (private) database timestamps, which is milliseconds based.
    public long getRecentlyPlayedCutoffTimeMillis() {
        return getCutoffTimeMillis(RECENTLY_PLAYED_CUTOFF);
    }

    private long getCutoffTimeMillis(final String cutoff) {
        final CalendarUtil calendarUtil = new CalendarUtil();
        long interval;

        switch (mPreferences.getString(cutoff, "")) {
            case "today":
                interval = calendarUtil.getElapsedToday();
                break;

            case "this_week":
                interval = calendarUtil.getElapsedWeek();
                break;

             case "past_seven_days":
                interval = calendarUtil.getElapsedDays(7);
                break;

            case "past_three_months":
                interval = calendarUtil.getElapsedMonths(3);
                break;

            case "this_year":
                interval = calendarUtil.getElapsedYear();
                break;

            case "this_month":
            default:
                interval = calendarUtil.getElapsedMonth();
                break;
        }

        return (System.currentTimeMillis() - interval);
    }

    public String getLastAddedCutoffText(Context context) {
        return getCutoffText(LAST_ADDED_CUTOFF, context);
    }

    public String getRecentlyPlayedCutoffText(Context context) {
        return getCutoffText(RECENTLY_PLAYED_CUTOFF, context);
    }

    private String getCutoffText(final String cutoff, Context context) {
        switch (mPreferences.getString(cutoff, "")) {
            case "today":
                return context.getString(R.string.today);

            case "this_week":
                return context.getString(R.string.this_week);

             case "past_seven_days":
                 return context.getString(R.string.past_seven_days);

            case "past_three_months":
                return context.getString(R.string.past_three_months);

            case "this_year":
                return context.getString(R.string.this_year);

            case "this_month":
            default:
                return context.getString(R.string.this_month);
        }
    }

    public int getLastSleepTimerValue() {
        return mPreferences.getInt(LAST_SLEEP_TIMER_VALUE, 30);
    }

    public void setLastSleepTimerValue(final int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(LAST_SLEEP_TIMER_VALUE, value);
        editor.apply();
    }

    public long getNextSleepTimerElapsedRealTime() {
        return mPreferences.getLong(NEXT_SLEEP_TIMER_ELAPSED_REALTIME, -1);
    }

    public void setNextSleepTimerElapsedRealtime(final long value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(NEXT_SLEEP_TIMER_ELAPSED_REALTIME, value);
        editor.apply();
    }

    public boolean getSleepTimerFinishMusic() {
        return mPreferences.getBoolean(SLEEP_TIMER_FINISH_SONG, false);
    }

    public void setSleepTimerFinishMusic(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(SLEEP_TIMER_FINISH_SONG, value);
        editor.apply();
    }

    public void setAlbumGridSize(final int gridSize) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(ALBUM_GRID_SIZE, gridSize);
        editor.apply();
    }



    public void setLastChangeLogVersion(int version) {
        mPreferences.edit().putInt(LAST_CHANGELOG_VERSION, version).apply();
    }

    public final int getLastChangelogVersion() {
        return mPreferences.getInt(LAST_CHANGELOG_VERSION, -1);
    }

    @SuppressLint("CommitPrefEdits")
    public void setIntroShown() {
        // don't use apply here
        mPreferences.edit().putBoolean(INTRO_SHOWN, true).commit();
    }

    public final boolean introShown() {
        return mPreferences.getBoolean(INTRO_SHOWN, false);
    }

    public final boolean rememberShuffle() {
        return mPreferences.getBoolean(REMEMBER_SHUFFLE, true);
    }

    public final String autoDownloadImagesPolicy() {
        return mPreferences.getString(AUTO_DOWNLOAD_IMAGES_POLICY, "only_wifi");
    }

 /*
        public final File getStartDirectory() {
        return new File(mPreferences.getString(START_DIRECTORY, FoldersFragment.getDefaultStartDirectory().getPath()));
    }
*/
    public void setStartDirectory(File file) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(START_DIRECTORY, FileUtil.safeGetCanonicalPath(file));
        editor.apply();
    }

    public final boolean synchronizedLyricsShow() {
        return mPreferences.getBoolean(SYNCHRONIZED_LYRICS_SHOW, true);
    }

    public void setInitializedBlacklist() {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(INITIALIZED_BLACKLIST, true);
        editor.apply();
    }

    public final boolean initializedBlacklist() {
        return mPreferences.getBoolean(INITIALIZED_BLACKLIST, false);
    }


    public final int getSongChildSortOrder() {
        return mPreferences.getInt(SONG_CHILD_SORT_ORDER,1);
    }

    public final void setSongChildSortOrder(int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(SONG_CHILD_SORT_ORDER, value);
        editor.apply();
    }

    public final void setIsUsingArtistImageAsBackground(boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(USE_ARTIST_IMAGE_AS_BACKGROUND,value);
        editor.apply();
    }

    public final boolean isUsingArtistImageAsBackground() {
        return mPreferences.getBoolean(USE_ARTIST_IMAGE_AS_BACKGROUND,true);
    }

    public final void setInAppVolume(float value) {
        if(value<0) value = 0;
        else if(value>1) value = 1;
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(IN_APP_VOLUME, value);
        editor.apply();
    }

    public final float getInAppVolume() {
        return mPreferences.getFloat(IN_APP_VOLUME,1);
    }

    public final void setMinDuration(int value) {
        if(value<0) value = 0;
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(AUDIO_MIN_DURATION, value);
        editor.apply();
    }

    public final int getMinDuration() {
        return mPreferences.getInt(AUDIO_MIN_DURATION,10000);
    }

    public SharedPreferences getSharePreferences() {
        return mPreferences;
    }

    public void notFirstTime() {
        setFirstTime(false);
    }

    public float getBalanceValue() {
        return mPreferences.getFloat(BALANCE_VALUE,0.5f);
    }

    public final void setBalanceValue(float value) {
        if(value<0) value = 0;
        else if(value>1) value = 1;
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(BALANCE_VALUE, value);
        editor.apply();
    }

    public final void setThreadNumber(int value) {
        if(value<=0) return;
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(THREAD_NUMBER,value);
        editor.apply();
    }

    public final int getThreadNumber() {
        return mPreferences.getInt(THREAD_NUMBER,6);
    }
}
