package com.ldt.musicr.ui.maintab.subpages.viewplaylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ldt.musicr.R;
import com.ldt.musicr.helper.EventListener;
import com.ldt.musicr.helper.Reliable;
import com.ldt.musicr.helper.ReliableEvent;

import java.util.List;

public class ViewPlaylistHeaderAdapter extends RecyclerView.Adapter<ViewPlaylistHeaderAdapter.HeaderViewHolder> {
    public static final String ACTION_CLICK_MENU = "action-click-menu";
    public static final String ACTION_CLICK_PLAY_ALL = "action-click-play-all";
    public static final String ACTION_CLICK_SHUFFLE = "action-click-shuffle";

    private ViewPlaylistViewModel.State mState;

    public ViewPlaylistViewModel.State getData() {
        return mState;
    }

    public void setData(ViewPlaylistViewModel.State state) {
        mState = state;
        notifyItemChanged(0);
    }

    public void setEventListener(EventListener<ViewPlaylistViewModel.State> eventListener) {
        mEventListener = eventListener;
    }

    public EventListener<ViewPlaylistViewModel.State> getEventListener() {
        return mEventListener;
    }

    private EventListener<ViewPlaylistViewModel.State> mEventListener;

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_playlist_header, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    final class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mAuthor;
        private TextView mTextAsIcon;
        private ImageView mIcon;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mAuthor = itemView.findViewById(R.id.author);
            mTextAsIcon = itemView.findViewById(R.id.text_as_icon);
            mIcon = itemView.findViewById(R.id.icon);
            itemView.findViewById(R.id.menu_button).setOnClickListener(this::clickMenu);
            itemView.findViewById(R.id.play_all_panel).setOnClickListener(this::clickPlayAll);
            itemView.findViewById(R.id.shuffle_play_button).setOnClickListener(this::clickShuffle);
        }

        void clickMenu(View v) {
            if (getEventListener() != null) {
                getEventListener().handleEvent(new ReliableEvent<>(Reliable.success(null), ACTION_CLICK_MENU));
            }
        }

        void clickPlayAll(View v) {
            if (getEventListener() != null) {
                getEventListener().handleEvent(new ReliableEvent<>(Reliable.success(null), ACTION_CLICK_PLAY_ALL));
            }
        }

        void clickShuffle(View v) {
            if (getEventListener() != null) {
                getEventListener().handleEvent(new ReliableEvent<>(Reliable.success(null), ACTION_CLICK_SHUFFLE));
            }
        }

        public void bind() {
            mTitle.setText(mState == null ? "" : mState.mTitle);
            mAuthor.setText(mState == null ? "" : mState.mDescription);
            mIcon.setImageBitmap(mState == null ? null : mState.mCoverImage);
        }
    }
}
