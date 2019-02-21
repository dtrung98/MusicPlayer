package com.ldt.musicr.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ldt.musicr.R;
import com.ldt.musicr.ui.main.navigate.library.LibraryTabFragment;
import com.ldt.musicr.ui.main.navigate.library.SongChildTab;

public class TestingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_layout);
        getSupportFragmentManager().beginTransaction().add(R.id.root,new LibraryTabFragment()).commit();
    }
}
