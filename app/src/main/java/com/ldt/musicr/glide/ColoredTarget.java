package com.ldt.musicr.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.ldt.musicr.R;
import com.ldt.musicr.glide.palette.BitmapPaletteTarget;
import com.ldt.musicr.glide.palette.BitmapPaletteWrapper;
import com.ldt.musicr.util.PhonographColorUtil;


public abstract class ColoredTarget extends BitmapPaletteTarget {
    public ColoredTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        onColorReady(getView().getContext().getResources().getColor(R.color.FlatBlue));
    }

    @Override
    public void onResourceReady(@NonNull BitmapPaletteWrapper resource, @Nullable com.bumptech.glide.request.transition.Transition<? super BitmapPaletteWrapper> transition) {
        super.onResourceReady(resource, transition);
        onColorReady(PhonographColorUtil.getColor(resource.getPalette(), getView().getContext().getResources().getColor(R.color.FlatBlue)/*getDefaultFooterColor()*/));
    }

  /*  protected int getDefaultFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), R.attr.defaultFooterColor);
    }

    protected int getAlbumArtistFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), R.attr.cardBackgroundColor);
    }*/

    public abstract void onColorReady(int color);
}
