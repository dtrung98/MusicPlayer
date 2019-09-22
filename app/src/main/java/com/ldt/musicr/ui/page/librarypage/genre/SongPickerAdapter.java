package com.ldt.musicr.ui.page.librarypage.genre;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ldt.musicr.R;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.ui.widget.bubblepicker.model.BubbleGradient;
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;
import com.ldt.musicr.util.MusicUtil;

public class SongPickerAdapter extends PickerAdapter<Song> {
    private Context mContext;
    private float oneDp = 1;
    private float unitSize = 45f; // 45dp for minimum size

    private final static String ROBOTO_BOLD = "roboto_bold.ttf";
    private final static String ROBOTO_MEDIUM = "roboto_medium.ttf";
    private final static String ROBOTO_REGULAR = "roboto_regular.ttf";

    public Typeface mBoldTypeface;
    public Typeface mMediumTypeface;
    public Typeface mRegularTypeface;

    private int[] mColors;

    private void init() {
        if (mContext != null) {
            AssetManager assets = mContext.getAssets();
            mBoldTypeface = Typeface.createFromAsset(assets, ROBOTO_BOLD);
            mMediumTypeface = Typeface.createFromAsset(assets, ROBOTO_MEDIUM);
            mRegularTypeface = Typeface.createFromAsset(assets, ROBOTO_REGULAR);

            mColors = mContext.getResources().getIntArray(R.array.colors);
            oneDp = mContext.getResources().getDimension(R.dimen.oneDP);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mContext = null;
    }


    public SongPickerAdapter(Context context) {
        mContext = context;
        init();
    }

    @Override
    public boolean onBindItem(PickerItem item, boolean create, int i) {
        Song song = mData.get(i);
        item.setTitle(song.title);
        item.setIconSizeUnit(PickerItem.SIZE_RANDOM);
        if(mColors!=null) {
            item.setGradient(new BubbleGradient(mColors[(i * 2) % mColors.length],
                    mColors[(i * 2) % 10 + 1], BubbleGradient.VERTICAL));
        }

        item.setTypeface(mMediumTypeface);
        item.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));

        // Glide
        Glide.with(mContext).load(MusicUtil.getMediaStoreAlbumCoverUri(song.albumId)).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                item.setBackgroundImage(resource);
                SongPickerAdapter.this.notifyBackImageUpdated(i);
            }
        });

        return true;
    }
}
