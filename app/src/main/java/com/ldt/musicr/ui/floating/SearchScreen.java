package com.ldt.musicr.ui.floating;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;

import com.ldt.musicr.R;
import com.ldt.musicr.service.MusicServiceEventListener;
import com.ldt.musicr.ui.page.MusicServiceNavigationFragment;

/**
 * Search screen allows user to search songs, playlists, artists
 */
public class SearchScreen extends MusicServiceNavigationFragment implements MusicServiceEventListener {
    private static final String TAG = "SearchScreen";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.screen_search, container, false);
    }

    private FrameLayout mContentView;

   /* @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static class RoundRectOutlineProvider extends ViewOutlineProvider {
        private int[] mRadius = new int[] {0,0,0,0};

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect();
        }
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentView = view.findViewById(R.id.contentView);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

         /*   final float curveRadius = 25 * mContentView.getResources().getDimension(R.dimen.oneDP);


            mContentView.setOutlineProvider(new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth()
                            , (int)(view.getHeight() + curveRadius), curveRadius);
                }

            });

            mContentView.setClipToOutline(true);*/



      /*      final Shape shape = new RoundRectShape();
            Drawable background = new ShapeDrawable(shape);
            background = DrawableCompat.wrap(background);
            background.setTint(Color.WHITE);
            mContentView.setBackground(background);
            mContentView.setClipToOutline(true);*/

            final RoundRectDrawable background;
            background = new RoundRectDrawable(ColorStateList.valueOf(Color.WHITE), 24 * mContentView.getResources().getDimension(R.dimen.oneDP));
            mContentView.setBackground(background);

            mContentView.setClipToOutline(true);
            mContentView.setElevation(4 * mContentView.getResources().getDimension(R.dimen.oneDP));

        }

    }
}
