package com.ldt.musicr.ui.page.subpages;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ldt.musicr.R;
import com.ldt.musicr.addon.lastfm.rest.LastFMRestClient;
import com.ldt.musicr.addon.lastfm.rest.model.LastFmArtist;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.ui.widget.fragmentnavigationcontroller.NavigationFragment;
import com.ldt.musicr.util.LastFMUtil;
import com.ldt.musicr.util.MusicUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class ArtistTrialPager extends NavigationFragment implements ResultCallback {
    private static final String TAG ="ArtistTrialPager";
    // we need these very low values to make sure our artist image loading calls doesn't block the image loading queue
    private static final int TIMEOUT = 750;

    private static final String ARTIST = "artist";

    public static ArtistTrialPager newInstance(Artist artist) {

        Bundle args = new Bundle();
        if(artist!=null)
            args.putParcelable(ARTIST,artist);

        ArtistTrialPager fragment = new ArtistTrialPager();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.image)
    ImageView mImage;

    @BindView(R.id.text)
    TextView mText;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.screen_tab_genre_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        init();
        mSwipeRefresh.setOnRefreshListener(this::updateData);
        mSwipeRefresh.setRefreshing(true);
        updateData();
    }

    private void updateData() {
        if(mArtist!=null) {
           new AsyncTask<Void,Void,Void>(){

               @Override
               protected Void doInBackground(Void... voids) {
                   tryToLoadArtistImage(mArtist,ArtistTrialPager.this);
                   return null;
               }
           }.execute();
        } else mSwipeRefresh.setRefreshing(false);
    }
    Artist mArtist;

    LastFMRestClient mLastFmClient;
    private void init() {
        Bundle bundle = getArguments();
        if(bundle!=null) {
            mArtist = bundle.getParcelable(ARTIST);
        }
        if(mArtist!=null)
        mLastFmClient = new LastFMRestClient(LastFMRestClient.createDefaultOkHttpClientBuilder(getContext())
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build());
    }
    private boolean isCancelled = false;
    private boolean mSkipOkHttpCache = false;
    private boolean mLoadOriginal = true;

    public void onSuccess(String url) {
        Log.d(TAG, "onSuccess: url = "+url);

         mText.post(new Runnable() {
             @Override
             public void run() {
                 Glide.with(ArtistTrialPager.this).load(url).into(mImage);
                 mText.setText(url);
                 mSwipeRefresh.setRefreshing(false);
             }
         });
    }

    @Override
    public void onSuccess(LastFmArtist lastFmArtist) {

    }

    @Override
    public void onFailure(Exception e) {
        Log.d(TAG, "onFailure: e = "+ e.getMessage());
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onSuccess(ArrayList<String> mResult) {
        if(mResult!=null) {
            if(!mResult.isEmpty()) {
                onSuccess(mResult.get(0));
                for (String url :
                        mResult) {
                    Log.d(TAG, "onSuccess: ["+url+"]");
                }
            }
        }
    }

    private void tryToLoadArtistImage(Artist artist, ResultCallback callback) {
        long start = System.currentTimeMillis();

        if (!MusicUtil.isArtistNameUnknown(artist.getName())/* && PreferenceUtil.isAllowedToDownloadMetadata(context)*/) {
            // Try to get the group image
            String artistNames = artist.getName();
            artistNames = artistNames
                    .replace(" ft "," & ")
                    .replace(";"," & ")
                    .replace(","," & ")
                    .replaceAll("( +)"," ").trim();
            Log.d(TAG, start + " afterArtist =["+ artistNames+"]");
            if(null==loadThisArtist(artistNames,callback)) return;
            if(null == loadThisArtist(artistNames.replace("&",", ").replaceAll("( +)"," ").trim().replaceAll("\\s+(?=[),])", ""),callback)) return;

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
                callback.onFailure(new NullPointerException("Artist is empty"));
            }
            for (String artistName : artists) {
                if (artistName.isEmpty()) {
                    e = new Exception("Empty Artist");
                    continue;
                }
                e = loadThisArtist(artistName.trim(), callback);
                if (e == null) break;
            }
            if (e != null) callback.onFailure(e);
        } else callback.onFailure(new Exception("Unknown Artist"));
    }

    private Exception loadThisArtist(String artistName, ResultCallback callback) {
        Response<LastFmArtist> response;
        try {
            response = mLastFmClient.getApiService().getArtistInfo(artistName, null, mSkipOkHttpCache ? "no-cache" : null).execute();
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(document!=null) {
                Element imageList = document.getElementsByClass("image-list").first();
                    if (imageList != null) {
                        Elements imageListItem = document.getElementsByClass("image-list-item");
                        if(imageListItem!=null) {
                            ArrayList<String > mResult = new ArrayList<>();
                            for (Element imageItem :
                                    imageListItem) {
                               Element image =  imageItem.selectFirst("img");
                               if(image!=null) {
                                  String url =  image.absUrl("src");
                                  mResult.add(url);
                               }
                            }
                            callback.onSuccess(mResult);
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
}

