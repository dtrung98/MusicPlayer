package com.ldt.musicr.helper.songpreview;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.ldt.musicr.model.Song;
import com.ldt.musicr.service.MusicPlayerRemote;
import com.ldt.musicr.service.MusicServiceEventListener;
import com.ldt.musicr.ui.widget.soundfile.SoundFile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SongPreviewController implements MusicServiceEventListener, SongPreviewListener {
    private static final String TAG = "SongPreviewController";

    public SongPreviewController() {
        mPreviewPlayer = new PreviewPlayer();
        mPreviewPlayer.setSongPreviewListener(this);
    }

    @Override
    public void onServiceConnected() {
    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayingMetaChanged() {

    }

    @Override
    public void onPlayStateChanged() {
        boolean isPlaying = MusicPlayerRemote.isPlaying();
        if (isPlaying && mPreviewPlayer != null && mPreviewPlayer.isPlayingPreview()) {
            mPreviewPlayer.pause();
        }
    }

    @Override
    public void onRepeatModeChanged() {
    }

    @Override
    public void onShuffleModeChanged() {
    }

    @Override
    public void onMediaStoreChanged() {
    }

    @Override
    public void onPaletteChanged() {

    }

    @Override
    public void onSongPreviewStart(PreviewSong song) {
        for (SongPreviewListener listener :
                mListeners) {
            listener.onSongPreviewStart(song);
        }
    }

    @Override
    public void onSongPreviewFinish(PreviewSong song) {
        for (SongPreviewListener listener :
                mListeners) {
            listener.onSongPreviewFinish(song);
        }
    }

    private ArrayList<SongPreviewListener> mListeners = new ArrayList<>();

    private PreviewPlayer mPreviewPlayer;

    public void destroy() {

        if (mSoundFilesTask != null) mSoundFilesTask.cancel();
        mSoundFilesTask = null;
        mPreviewPlayer.destroy();
        mPreviewPlayer = null;
        mListeners.clear();

    }

    public void addSongPreviewListener(SongPreviewListener listener) {
        if (listener != null && !mListeners.contains(listener))
            mListeners.add(listener);
    }

    public void removeAudioPreviewerListener(SongPreviewListener listener) {
        if (listener != null) mListeners.remove(listener);
    }

    public void previewSongs(List<Song> songs) {
        Log.d(TAG, "previewSongs: size = " + songs.size());
        previewSongs(songs.toArray(new Song[0]));
    }

    public boolean isPlayingPreview() {
        return mPreviewPlayer.isPlayingPreview();
    }

    public Song getCurrentSongPreviewSong() {
        PreviewSong song = mPreviewPlayer.getCurrentPreviewSong();
        if (song == null) return null;
        return song.getSong();
    }

    public PreviewSong getCurrentPreviewSong() {
        return mPreviewPlayer.getCurrentPreviewSong();
    }

    public boolean isPreviewingSong(Song which) {
        Song song = getCurrentSongPreviewSong();
        return song != null && song.equals(which);
    }

    public void previewSongs(Song... songs) {
        boolean isMusicPlaying = MusicPlayerRemote.isPlaying();

        if (mSoundFilesTask != null) mSoundFilesTask.cancel();
        mPreviewPlayer.stopSession();

        if (isMusicPlaying) {
            MusicPlayerRemote.playOrPause();
        }
        mPreviewPlayer.shouldPlayingMusicServiceOnFinish(isMusicPlaying);


        mSoundFilesTask = new SoundFilesLoader(this);
        mSoundFilesTask.execute(songs);
    }

    public void cancelPreview() {
        if (mSoundFilesTask != null) mSoundFilesTask.cancel();
        mSoundFilesTask = null;
        mPreviewPlayer.stopSession();
    }

    private SoundFilesLoader mSoundFilesTask;

    private static class SoundFilesLoader extends AsyncTask<Song, Void, Void> {
        private final WeakReference<SongPreviewController> mRefController;

        public SoundFilesLoader(SongPreviewController controller) {
            mRefController = new WeakReference<>(controller);
        }

        private boolean mIsCanceled = false;

        public void cancel() {
            cancel(true);
            mRefController.clear();
            mIsCanceled = true;
        }

        @Override
        protected Void doInBackground(Song... songs) {

            for (Song song :
                    songs) {
                if (mIsCanceled) break;
                loadPreviewSong(song);
            }

            return null;
        }

        private void loadPreviewSong(Song song) {
            final long start = System.currentTimeMillis();
            SoundFile cheapSoundFile = null;
            try {
                cheapSoundFile = SoundFile.create(song, null);
            } catch (final Exception e) {
                return;
            }

            final long middle = System.currentTimeMillis();

            if (cheapSoundFile != null)
                try {
                    calculateSound(cheapSoundFile);
                } catch (final Exception e) {
                    return;
                }

            final long end = System.currentTimeMillis();
            Log.d(TAG, ("loadPreviewSong costs " + (end - start) + "ms: creating sound file costs " + (middle - start) + ", calculating costs " + (end - middle)));

            SongPreviewController controller = mRefController.get();
            if (controller != null && !mIsCanceled) {
                controller.mHandler.post(
                        () -> controller.onNewPreviewSongReady(
                                new PreviewSong(song, (int) mMillisPlayFrom, (int) mMillisPlayTo)));
            }
        }

        private double mMillisPlayFrom = 0;
        private double mMillisPlayTo = 0;

        private double mSampleRate;
        private double mSamplesPerFrame;
        private int mNumFrames;
        private double mDuration;
        private int mIntDuration;

        private int[] mFrameGain;

        private synchronized void calculateSound(SoundFile soundFile) {
            // run in the background
            mNumFrames = soundFile.getNumFrames();
            mSampleRate = soundFile.getSampleRate();
            mSamplesPerFrame = soundFile.getSamplesPerFrame();
            mDuration = mNumFrames * mSamplesPerFrame / mSampleRate + 0.0f;
            mIntDuration = (int) mDuration;
            mFrameGain = soundFile.getFrameGains();


            double durationZone;
            double timeFromZone = 0;

            if (mDuration < 40) {
                durationZone = 40;
                timeFromZone = 5;
            } else if (mDuration < 60) {
                timeFromZone = 5;
                durationZone = mIntDuration - 5 - 5;
            } else {
                timeFromZone = 13;
                durationZone = mIntDuration - timeFromZone - 8;
            }

            int frameFromZone = (int) (mNumFrames * timeFromZone / mDuration);
            //   int frameToZone = (int) (frameFromZone + durationZone*2);
            //   int frameToZone = (int) (mNumFrames-1);
            int frameToZone = (int) (frameFromZone + durationZone * mNumFrames / mDuration);
            if (frameToZone > mNumFrames - 1) frameToZone = (int) (mNumFrames - 1);

            int[] frameGainInPlayingZone = Arrays.copyOfRange(mFrameGain, frameFromZone, frameToZone);

            int numberFrameGainPlayingZone = frameGainInPlayingZone.length;


            // reduce the FrameGain to PenGain
            // 1 pen gain = 0.5s
            float penGainSecond_Guessing = 0.5f;
            durationZone = ((frameToZone - frameFromZone) / (mNumFrames + 0f)) * mDuration;
            int numberPenGain_Guess = (int) (Math.floor(((frameToZone - frameFromZone) / (mNumFrames + 0f)) * (mDuration / penGainSecond_Guessing))); // each 500ms
            int numberFrameEachPenGain = numberFrameGainPlayingZone / numberPenGain_Guess;

            numberPenGain_Guess = (int) Math.ceil((numberFrameGainPlayingZone + 0f) / numberFrameEachPenGain);
            float penGainSecond = (float) (durationZone / numberPenGain_Guess);

            double[] originalPenGain = new double[numberPenGain_Guess];

            originalPenGain[0] = 0;
            int iPen = 0;
            int pos = 0;
            for (int iFrame = 0; iFrame < numberFrameGainPlayingZone; iFrame++) {
                originalPenGain[iPen] += frameGainInPlayingZone[iFrame];
                pos++;
                if (iPen == numberPenGain_Guess - 1)
                    Log.d(TAG, "last : frame[" + iFrame + "] = " + frameGainInPlayingZone[iFrame] + ", pos = " + pos);

                if (iFrame == numberFrameGainPlayingZone - 1) {
                    originalPenGain[iPen] /= pos;
                    Log.d(TAG, "last : ipen = " + iPen + ", average = " + originalPenGain[iPen] + ", from " + pos + " element" + " / " + numberFrameEachPenGain);
                } else if (pos == numberFrameEachPenGain) {
                    originalPenGain[iPen] /= numberFrameEachPenGain;

                    pos = 0;
                    iPen++;
                }
            }
            Log.d(TAG, "calculateSound: duration = " + mDuration);

            // make pen gains smoothly
            // computeDoublesForAllZoomLevels(numberPenGain_Guess, originalPenGain);
            double[] SmoothedPenGain = originalPenGain;// new double[numberPenGain_Guess];
//        for (int i_pen = 0; i_pen < numberPenGain_Guess; i_pen++)
//            SmoothedPenGain[i_pen] = getHeight(i_pen, numberPenGain_Guess, originalPenGain, scaleFactor, minGain, range);

            // now explore the smoothedPenGain

        /*
        double maxAverage = findMaxAverage(SmoothedPenGain,15*numberFrameEachPenGain);
         */

            final int staticPenSize = 24;
            final int staticEdge = 22;

            int startPen = findMaxAverage(SmoothedPenGain, SmoothedPenGain.length, staticPenSize);
            int endPen = startPen + staticPenSize;

            int newStartPen = startPen;
            int newEndPen = endPen;

            double minPenBeforeStartPen = SmoothedPenGain[startPen];
            int item = (startPen <= staticEdge) ? 0 : startPen - staticEdge;
            for (; item < startPen; item++)
                if (SmoothedPenGain[item] < minPenBeforeStartPen) {
                    minPenBeforeStartPen = SmoothedPenGain[item];
                    newStartPen = item;
                }

            double minPenAfterEndPen = SmoothedPenGain[endPen];
            for (int i = endPen + 1; i <= endPen + staticEdge && i < SmoothedPenGain.length; i++)
                if (SmoothedPenGain[i] < minPenAfterEndPen) {
                    minPenAfterEndPen = SmoothedPenGain[i];
                    newEndPen = i;
                }

            Log.d(TAG, "calculateSound: start = " + newStartPen + ", penNum = " + (newEndPen - newStartPen));

            mMillisPlayFrom = 1000 * (timeFromZone + newStartPen * penGainSecond);
            mMillisPlayTo = 1000 * (timeFromZone + newEndPen * penGainSecond);
            Log.d(TAG, "calculateSound: time from " + mMillisPlayFrom + " to " + mMillisPlayTo);
        }


        // Returns beginning index of maximum average
        // subarray of length 'k'
        static int findMaxAverage(double[] arr, int n, int k) {

            // Check if 'k' is valid
            if (k > n)
                return -1;

            // Compute sum of first 'k' elements
            double sum = arr[0];
            for (int i = 1; i < k; i++)
                sum += arr[i];

            double max_sum = sum;
            int max_end = k - 1;

            // Compute sum of remaining subarrays
            for (int i = k; i < n; i++) {
                sum = sum + arr[i] - arr[i - k];
                if (sum > max_sum) {
                    max_sum = sum;
                    max_end = i;
                }
            }

            // Return starting index
            //return max_end - k + 1;
            return max_end - k;
        }

        protected double getHeight(int i, int totalPens, double[] penGain, float scaleFactor, float minGain, float range) {
            double value = (getGain(i, totalPens, penGain) * scaleFactor - minGain) / range;

            if (value < 0.0)
                value = 0.0f;
            if (value > 1.0)
                value = 1.0f;
            value = (value + 0.05) / 1.05f;
            return value;
        }

        protected double getGain(int i, int totalPens, double[] penGain) {
            int x = Math.min(i, totalPens - 1);
            if (totalPens < 2) {
                return penGain[x];
            } else {
                if (x == 0) {
                    return (penGain[0] / 2.0f) + (penGain[1] / 2.0f);
                } else if (x == totalPens - 1) {
                    return (penGain[totalPens - 2] / 2.0f) + (penGain[totalPens - 1] / 2.0f);
                } else {
                    return (penGain[x - 1] / 3.0f) + (penGain[x] / 3.0f) + (penGain[x + 1] / 3.0f);
                }
            }
        }

    }

    private Handler mHandler = new Handler();

    private void onNewPreviewSongReady(PreviewSong song) {
        mPreviewPlayer.addToQueue(song);
    }
}
