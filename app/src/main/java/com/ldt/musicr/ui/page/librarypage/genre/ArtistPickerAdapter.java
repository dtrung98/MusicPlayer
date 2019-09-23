package com.ldt.musicr.ui.page.librarypage.genre;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

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
import com.ldt.musicr.ui.widget.bubblepicker.rendering.PickerAdapter;

public class ArtistPickerAdapter extends PickerAdapter<Artist> {

    public ArtistPickerAdapter(Context context) {
        super(context);
    }

    @Override
    public boolean onBindItem(PickerItem item, boolean create, int i) {
        Artist artist = mData.get(i);
        item.setTitle(artist.getName());
        item.setIconSizeUnit(PickerItem.SIZE_RANDOM);
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
