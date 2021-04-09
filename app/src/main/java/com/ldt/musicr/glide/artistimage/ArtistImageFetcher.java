package com.ldt.musicr.glide.artistimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;

import com.ldt.musicr.addon.lastfm.rest.LastFMRestClient;
import com.ldt.musicr.addon.lastfm.rest.model.LastFmArtist;
import com.ldt.musicr.util.LastFMUtil;
import com.ldt.musicr.util.MusicUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

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
    private final  boolean mLoadOriginal;
    private final int mImageNumber;

    public ArtistImageFetcher(LastFMRestClient lastFMRestClient, ArtistImage model, ModelLoader<GlideUrl, InputStream> urlLoader, int width, int height, Options options) {
        this.lastFMRestClient = lastFMRestClient;
        this.model = model;
        this.urlLoader = urlLoader;
        this.width = width;
        this.height = height;
        mOption = options;
        mLoadOriginal = model.mLoadOriginal;
        mImageNumber = model.mImageNumber;
    }

    private Exception loadThisArtistWithJSoup(String artistName, @NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
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
            Log.d(TAG, "loadThisArtist: "+response.toString());
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

         /*     Response<String> photoPage = null;
                try {
                    photoPage = mLastFmClient.getApiService().getPhotoPage(lastFmArtist.getArtist().getUrl()+"/+images",null,mSkipOkHttpCache ? "no-cache" : null).onRunning();
                } catch (IOException e) {
                    e.printStackTrace();
                    return e;
                }
                Log.d(TAG, "loadThisArtist: "+photoPage.body());*/
                //  if(photoPage.body()!=null) {
                Document document = null;
                try {
                    document = Jsoup.connect(lastFmArtist.getArtist().getUrl()+"/+images").get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(document!=null) {
                    Element imageList = document.getElementsByClass("image-list").first();
                    if (imageList != null) {
                        Elements imageListItem = document.getElementsByClass("image-list-item");
                        if(imageListItem!=null) {
                            ArrayList<String > result = new ArrayList<>();
                            for (Element imageItem :  imageListItem) {
                                Element image =  imageItem.selectFirst("img");
                                if(image!=null) {
                                    String url =  image.absUrl("src");
                                    result.add(url);
                                }
                            }
                            if(result.isEmpty()) return new Exception("Empty Array List");

                            String urlString;
                            switch (mImageNumber) {
                                case ArtistImage.RANDOM:
                                    Random random = new Random();
                                    urlString = result.get(random.nextInt(result.size()));
                                    break;
                                 case ArtistImage.FIRST:
                                     urlString = result.get(0);
                                     break;
                                  default:
                                      if(mImageNumber>result.size()-1) urlString = result.get(result.size()-1);
                                      else urlString = result.get(mImageNumber);
                            }

                            if(mLoadOriginal) urlString = findAndReplaceToGetOriginal(urlString);
                            Log.d(TAG, "loadThisArtist: url = ["+urlString+"]");
                            GlideUrl url = new GlideUrl(urlString);

                            ModelLoader.LoadData<InputStream> loadData = urlLoader.buildLoadData(url, width, height, mOption);
                            if (loadData == null)
                                return  new IOException("Load data fails");
                            else {
                                try {
                                    urlFetcher = loadData.fetcher;
                                    DataCallback<? super InputStream> innerCallback = new DataCallback<InputStream>() {
                                        @Override
                                        public void onDataReady(@Nullable InputStream data) {
                                            Log.d(TAG, "onDataReady");
                                            callback.onDataReady(data);
                                        }

                                        @Override
                                        public void onLoadFailed(@NonNull Exception e) {
                                            Log.d(TAG, "onLoadFailed: e = "+e.getClass()+" | "+e.getMessage());
                                            callback.onLoadFailed(e);
                                        }
                                    };
                                    Log.d(TAG, "loadThisArtist: start");
                                    urlFetcher.loadData(priority, innerCallback);
                                    Log.d(TAG, "loadThisArtist: end");
                                } catch (Exception e) {
                                    return e;
                                }
                            }
                            return null;
                        }
                    }
                }
                return new NullPointerException("Photo page body is null");
/*
                if(mLoadOriginal) largestArtistImageUrl = ArtistImageFetcher.findAndReplaceToGetOriginal(largestArtistImageUrl);
                Log.d(TAG, "loadThisArtist: url = ["+largestArtistImageUrl+"]");
                callback.onSuccess(largestArtistImageUrl);*/
            } else return new Exception("No Artist Image is available : \n"+lastFmArtist.toString());
        }
    }

    public static String findAndReplaceToGetOriginal(String url) {
        return url.replaceAll("([0-9]+x[0-9]+|avatar170s)","770x0");
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
            if(null==loadThisArtistWithJSoup(artistNames,priority,callback)) return;
            if(null == loadThisArtistWithJSoup(artistNames.replace("&",", ").replaceAll("( +)"," ").trim().replaceAll("\\s+(?=[),])", ""),priority,callback)) return;

            // if not, try to get one of artist image
            Exception e = null;
            String[] artists = artistNames.split("&");

            StringBuilder log = new StringBuilder();
            for (String a :
                    artists) {
                log.append(" [").append(a).append("] ");
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
                e = loadThisArtistWithJSoup(artistName.trim(), priority, callback);
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
