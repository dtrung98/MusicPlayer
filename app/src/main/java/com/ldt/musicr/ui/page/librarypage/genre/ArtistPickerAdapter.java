package com.ldt.musicr.ui.page.librarypage.genre;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ldt.musicr.R;
import com.ldt.musicr.glide.ArtistGlideRequest;
import com.ldt.musicr.glide.GlideApp;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.ui.widget.bubblepicker.model.BubbleGradient;
import com.ldt.musicr.ui.widget.bubblepicker.model.PickerItem;

public class ArtistPickerAdapter extends PickerAdapter<Artist>{
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
        mContext = null;
    }

    public ArtistPickerAdapter(Context context) {
        mContext = context;
        init();
    }

    @Override
    public boolean onBindItem(PickerItem item, boolean create, int i) {
        Artist artist = mData.get(i);
        item.setTitle(artist.getName());
        item.setIconSizeUnit(PickerItem.SIZE_RANDOM);
        if(mColors!=null) {
            item.setGradient(new BubbleGradient(mColors[(i * 2) % mColors.length],
                    mColors[(i * 2) % 10 + 1], BubbleGradient.VERTICAL));
        }

        item.setTypeface(mMediumTypeface);
        item.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));

        // Glide
        ArtistGlideRequest.Builder.from(GlideApp.with(mContext), artist)
                // .tryToLoadOriginal(true)
                .generateBuilder(mContext)
                .buildRequestDrawable()
                .centerCrop()
               // .error(R.drawable.music_style)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        item.setBackgroundImage(resource);
                    }
                });

        return true;
    }

}
