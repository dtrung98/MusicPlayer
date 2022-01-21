package com.ldt.musicr.ui.maintab.library.genre;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ldt.musicr.R;
import com.ldt.musicr.ui.maintab.MusicServiceFragment;

import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GenreChildTab extends MusicServiceFragment {
    public static final String TAG="GenreChildTab";

    public static GenreChildTab newInstance() {
        return new GenreChildTab();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.screen_tab_genre_list,container,false);
    }

    @BindDimen(R.dimen.bottom_back_stack_spacing)
    float mMinBottomPadding;

    @BindDimen(R.dimen._16dp)
    float m16Dp;

    private Unbinder mUnbinder;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this,view);
    }

    @Override
    public void onMediaStoreChanged() {}

    @Override
    public void onDestroyView() {

        if(mUnbinder!=null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        super.onDestroyView();
    }
}

