package com.ldt.musicr.ui.tabs.library;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ldt.musicr.R;
import com.ldt.musicr.glide.ArtistGlideRequest;
import com.ldt.musicr.glide.ColoredTarget;
import com.ldt.musicr.model.Artist;
import com.ldt.musicr.model.Genre;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemHolder> implements FastScrollRecyclerView.SectionedAdapter {
    private static final String TAG = "ArtistAdapter";

    Context mContext;
    ArrayList<Artist> mData = new ArrayList<>();
    ArrayList<Genre>[] mGenres;

    ArtistAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<Artist> data, ArrayList<Genre>[] genres) {
        mData.clear();
        if(data!=null) {
            mData.addAll(data);
            mGenres = genres;
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_child,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        if(mGenres!=null&&mGenres.length>i-1)
        itemHolder.bind(mData.get(i), mGenres[i]);
        else itemHolder.bind(mData.get(i),null);
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }
    public static int lighter(int color, float factor, int alpha) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    @NonNull
    @Override
    public String getSectionName(int i) {
        if(mData.get(i).getName().isEmpty())
            return "";
        return mData.get(i).getName().substring(0,1);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mImage.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, view.getWidth(), view.getHeight());
                    }
                });
                mImage.setClipToOutline(true);
            }
        }
        @BindView(R.id.artist)
        TextView mArtist;

        @BindView(R.id.image)
        ImageView mImage;

        @BindView(R.id.genre_one)
        TextView mGenreOne;

        @BindView(R.id.genre_two)
        TextView mGenreTwo;

        @BindView(R.id.panel)
        View mPanel;
        @BindView(R.id.panel_color)
        View mPanelColor;

        @OnClick(R.id.panel)
        void goToThisArtist() {

        }
        @BindView(R.id.root)
        View mRoot;

        @BindView(R.id.count)
        TextView mCount;

        public void bind(Artist artist, ArrayList<Genre> genres) {
            long start = System.currentTimeMillis();
            mArtist.setText(artist.getName());
            mCount.setText(String.format("%d %s", artist.songCount, mCount.getContext().getResources().getString(R.string.songs)));

            if(genres==null||genres.isEmpty()) {
                mGenreOne.setText("Unknown Genres");
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.GONE);
            } else if(genres.size()==1) {
                mGenreOne.setText(genres.get(0).name);
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.GONE);
            } else {
                mGenreOne.setText(genres.get(0).name);
                mGenreTwo.setText(genres.get(0).name);
                mGenreOne.setVisibility(View.VISIBLE);
                mGenreTwo.setVisibility(View.VISIBLE);
            }
            long start2 = System.currentTimeMillis() - start;
            try {
                loadArtistImage(artist);
            } catch (Exception ignored) {}
            long start3 = System.currentTimeMillis() - start - start2;
            Log.d(TAG, "bind: start2 = "+ start2+", start3 = "+ start3);
        }
        private void loadArtistImage(Artist artist) {
           String[] artists = artist.getName().replace(';',',').split("\\s*,\\s*");
           Artist artistTemp;
           if(artists.length>1)
               artistTemp = new Artist(artist.id,artists[0],artist.albumCount,artist.songCount);
           else artistTemp = artist;


            ArtistGlideRequest.Builder.from(Glide.with(mContext), artistTemp)//.build().into(mImage);
                    .generatePalette(mContext).build()
                    .into(new ColoredTarget(mImage) {
                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                            super.onLoadCleared(placeholder);
//                            setColors(getDefaultFooterColor(), holder);
                        }

                        @Override
                        public void onColorReady(int color) {
                            int fixedColor = lighter(color,0.55f,0x90);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                mPanelColor.getBackground().setTint(fixedColor);
                            else mPanelColor.getBackground().setColorFilter(fixedColor, PorterDuff.Mode.SRC_ATOP);
//
//                            if (usePalette)
//                                setColors(color, holder);
//                            else
//                                setColors(getDefaultFooterColor(), holder);
                        }
                    });
        }
    }
}
