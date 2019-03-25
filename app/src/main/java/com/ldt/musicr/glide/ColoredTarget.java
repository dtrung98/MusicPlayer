package com.ldt.musicr.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.ldt.musicr.R;
import com.ldt.musicr.util.PhonographColorUtil;


public abstract class ColoredTarget extends BitmapPaletteTarget {
    public ColoredTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        onColorReady(getView().getContext().getResources().getColor(R.color.FlatBlue));
    }

    @Override
    public void onResourceReady(BitmapPaletteWrapper resource, GlideAnimation<? super BitmapPaletteWrapper> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
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
