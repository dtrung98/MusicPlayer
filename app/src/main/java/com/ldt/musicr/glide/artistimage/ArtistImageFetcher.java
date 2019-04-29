package com.ldt.musicr.glide.artistimage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;

import com.ldt.musicr.addon.lastfm.rest.LastFMRestClient;
import com.ldt.musicr.addon.lastfm.rest.model.LastFmArtist;
import com.ldt.musicr.util.LastFMUtil;
import com.ldt.musicr.util.MusicUtil;

import java.io.IOException;
import java.io.InputStream;

import retrofit2.Response;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class ArtistImageFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "ArtistImageFetcher";
    private final LastFMRestClient lastFMRestClient;
    private final ArtistImage model;
    private ModelLoader<GlideUrl, InputStream> urlLoader;
    private final int width;
    private final int height;
    private volatile boolean isCancelled;
    private DataFetcher<InputStream> urlFetcher;
    private final Options mOption;
    private boolean mLoadBiggest = false;

    public ArtistImageFetcher(LastFMRestClient lastFMRestClient, ArtistImage model, ModelLoader<GlideUrl, InputStream> urlLoader, int width, int height, Options options) {
        this.lastFMRestClient = lastFMRestClient;
        this.model = model;
        this.urlLoader = urlLoader;
        this.width = width;
        this.height = height;
        mOption = options;
    }
    private Exception loadThisArtist(String artistName, @NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        Response<LastFmArtist> response;
        try {
            response = lastFMRestClient.getApiService().getArtistInfo(artistName, null, model.mSkipOkHttpCache ? "no-cache" : null).execute();
            Log.d(TAG, "loadData: artistName = ["+artistName+"] : succeed");
        } catch (Exception e) {
            Log.d(TAG, "loadData: artistName = ["+artistName+"] : exception");
            return e;
        }

        if(!response.isSuccessful())
            return new IOException("Request failed with code: " + response.code());
        else {
            LastFmArtist lastFmArtist = response.body();
            Log.d(TAG, "loadData: "+lastFmArtist);

            if (isCancelled) {
                return new Exception("Cancelled");
            }

            if (lastFmArtist == null || lastFmArtist.getArtist() == null) {
                return new NullPointerException("Artist is null");
            }

            String largestArtistImageUrl = LastFMUtil.getLargestArtistImageUrl(lastFmArtist.getArtist().getImage());
            if(largestArtistImageUrl!=null&&!largestArtistImageUrl.isEmpty()) {
                Log.d(TAG, "loadThisArtist: url = ["+largestArtistImageUrl+"]");
                GlideUrl url = new GlideUrl(largestArtistImageUrl);

                ModelLoader.LoadData<InputStream> loadData = urlLoader.buildLoadData(url, width, height, mOption);
                if (loadData == null)
                    return  new IOException("Load data fails");
                else {
                    try {
                        urlFetcher = loadData.fetcher;
                        urlFetcher.loadData(priority, callback);
                    } catch (Exception e) {
                        return e;
                    }
                }
            } else return new Exception("No Artist Image is available : \n"+lastFmArtist.toString());
        }
        return null;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        long start = System.currentTimeMillis();
        if (!MusicUtil.isArtistNameUnknown(model.mArtistName)/* && PreferenceUtil.isAllowedToDownloadMetadata(context)*/) {
           // Try to get the group image
            String artistNames = model.getArtistName();
            artistNames = artistNames
                    .replace(" ft "," & ")
                    .replace(";"," & ")
                    .replace(","," & ")
                    .replaceAll("( +)"," ").trim();
            Log.d(TAG, start + " afterArtist =["+ artistNames+"]");
            if(null==loadThisArtist(artistNames,priority,callback)) return;
            if(null == loadThisArtist(artistNames.replace("&",", ").replaceAll("( +)"," ").trim().replaceAll("\\s+(?=[),])", ""),priority,callback)) return;

            // if not, try to get one of artist image
            Exception e = null;
            String[] artists = artistNames.split("&");

            String log = "";
            for (String a :
                    artists) {
                log+=" ["+a+"] ";
            }
            Log.d(TAG, start+" afterSplit ="+log);

            if (artists.length == 0) {
                callback.onLoadFailed(new NullPointerException("Artist's empty"));
            }
            for (String artistName : artists) {
                if (artistName.isEmpty()) {
                    e = new Exception("Empty Artist");
                    continue;
                }
                e = loadThisArtist(artistName.trim(), priority, callback);
                if (e == null) break;
            }
            if (e != null) callback.onLoadFailed(e);
        } else callback.onLoadFailed(new Exception("Unknown Artist"));
    }

    @Override
    public void cleanup() {
        if (urlFetcher != null) {
            urlFetcher.cleanup();
        }
    }

    @Override
    public void cancel() {
        isCancelled = true;
        if (urlFetcher != null) {
            urlFetcher.cancel();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
