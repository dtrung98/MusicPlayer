package com.ldt.musicr.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldt.musicr.App;
import com.ldt.musicr.loader.medialoader.SongLoader;
import com.ldt.musicr.ui.widget.UpdateToastMediaScannerCompletionListener;
import com.ldt.musicr.ui.widget.dialog.LoadingScreenDialog;
import com.ldt.musicr.util.MusicUtil;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WriteTagDialog extends LoadingScreenDialog {
    public static final String TAG = "WriteTagDialog";

    public interface WriteTagResultListener {
        void onWritingTagFinish(boolean result);
    }

    private WriteTagResultListener mListener;
    public WriteTagDialog setWritingTagResultListener(WriteTagResultListener listener) {
        mListener = listener;
        return this;
    }

    public void removeWriteTagResultListener() {
        mListener = null;
    }
    private LoadingInfo mLoadingInfo;

    public static WriteTagDialog newInstance(@NonNull LoadingInfo loadingInfo) {

        WriteTagDialog fragment = new WriteTagDialog();
        fragment.mLoadingInfo = loadingInfo;
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        runTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void runTask() {
        if(mTask!=null) mTask.cancel();
        mTask = new WriteTagsAsyncTask(this);
        mTask.execute(mLoadingInfo);
    }

    public static class LoadingInfo {
        public final Collection<String> filePaths;
        @Nullable
        public final Map<FieldKey, String> fieldKeyValueMap;
        @Nullable
        private ArtworkInfo artworkInfo;

        public LoadingInfo(Collection<String> filePaths, @Nullable Map<FieldKey, String> fieldKeyValueMap, @Nullable ArtworkInfo artworkInfo) {
            this.filePaths = filePaths;
            this.fieldKeyValueMap = fieldKeyValueMap;
            this.artworkInfo = artworkInfo;
        }
    }

    @Override
    public void onDestroyView() {
        if(mTask!=null) {
            mTask.cancel();
            mTask = null;
        }

        mHandler = null;
        mListener =null;
        super.onDestroyView();
    }

    private WriteTagsAsyncTask mTask = null;
    private Handler mHandler = new Handler();

    protected void postOnWritingFinish(String message, String[] fileToBeScanned) {
        if(mHandler!=null) mHandler.post(() -> finishWriting(message, fileToBeScanned));
    }

    private void finishWriting(String message, String[] fileToBeScanned) {
        scan(fileToBeScanned);
        if(message==null || !message.isEmpty()) {
            if(mListener!=null) mListener.onWritingTagFinish(false);
            showFailureThenDismiss(message);
        } else {
            if(mListener!=null) mListener.onWritingTagFinish(true);
            showSuccessThenDismiss(null);
        }
    }

    private void scan(String[] fileToBeScanned) {
        Context context = getContext();
        if(context==null) context = App.getInstance().getApplicationContext();
        MediaScannerConnection.scanFile(context, fileToBeScanned, null, context instanceof Activity ? new UpdateToastMediaScannerCompletionListener((Activity) context, fileToBeScanned) : null);
    }

    @NonNull
    public static List<String> getSongPaths(Context context, int id) {
        ArrayList<String> paths = new ArrayList<>(1);
        paths.add(SongLoader.getSong(context, id).data);
        return paths;
    }

    public static class ArtworkInfo {
        public final int albumId;
        public final Bitmap artwork;

        public ArtworkInfo(int albumId, Bitmap artwork) {
            this.albumId = albumId;
            this.artwork = artwork;
        }
    }

    private static class WriteTagsAsyncTask extends AsyncTask<LoadingInfo, Integer, Void> {

        private final WeakReference<WriteTagDialog> mWeakDialog;

        public WriteTagsAsyncTask(WriteTagDialog dialog) {
            mWeakDialog = new WeakReference<>(dialog);
        }

        private Context getContext() {
            WriteTagDialog dialog = mWeakDialog.get();
            if(dialog!=null) return dialog.getContext();
            return null;
        }

        public void cancel() {
            cancel(true);
        }

        private void finish(String message, String[] fileToBeScanned) {
            WriteTagDialog dialog = mWeakDialog.get();
            if(dialog!=null) dialog.postOnWritingFinish(message, fileToBeScanned);
        }

        @Override
        protected Void doInBackground(LoadingInfo... params) {
            String mMessage ="";
            try {
                LoadingInfo info = params[0];

                Artwork artwork = null;
                File albumArtFile = null;
                if (info.artworkInfo != null && info.artworkInfo.artwork != null) {
                    try {
                        albumArtFile = MusicUtil.createAlbumArtFile().getCanonicalFile();
                        info.artworkInfo.artwork.compress(Bitmap.CompressFormat.PNG, 0, new FileOutputStream(albumArtFile));
                        artwork = ArtworkFactory.createArtworkFromFile(albumArtFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mMessage = "Something wrong when writing album art";
                    }
                }

                int counter = 0;
                boolean wroteArtwork = false;
                boolean deletedArtwork = false;
                for (String filePath : info.filePaths) {
                    publishProgress(++counter, info.filePaths.size());
                    try {
                        AudioFile audioFile = AudioFileIO.read(new File(filePath));
                        Tag tag = audioFile.getTagOrCreateAndSetDefault();

                        if (info.fieldKeyValueMap != null) {
                            for (Map.Entry<FieldKey, String> entry : info.fieldKeyValueMap.entrySet()) {
                                try {
                                    tag.setField(entry.getKey(), entry.getValue());
                                } catch (Exception e) {
                                    if(mMessage.isEmpty())
                                    mMessage = "Something wrong when writing tag";
                                }
                            }
                        }

                        if (info.artworkInfo != null) {
                            if (info.artworkInfo.artwork == null) {
                                tag.deleteArtworkField();
                                deletedArtwork = true;
                            } else if (artwork != null) {
                                tag.deleteArtworkField();
                                tag.setField(artwork);
                                wroteArtwork = true;
                            }
                        }

                        audioFile.commit();
                    } catch (@NonNull Exception e) {
                        e.printStackTrace();
                        mMessage = e.getMessage();
                    }
                }

                Context context = getContext();
                if (context != null) {
                    if (wroteArtwork) {
                        MusicUtil.insertAlbumArt(context, info.artworkInfo.albumId, albumArtFile.getPath());
                    } else if (deletedArtwork) {
                        MusicUtil.deleteAlbumArt(context, info.artworkInfo.albumId);
                    }
                }

                finish(mMessage, info.filePaths.toArray(new String[0]));
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                mMessage = e.getMessage();
                finish(mMessage, null);
                return null;
            }
        }

    }
}
