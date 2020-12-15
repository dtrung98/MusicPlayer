package com.ldt.musicr.ui.base.presentationstyle;

import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * Attribute of a {@link Attribute} which is saved/restored by {@link com.ldt.musicr.ui.base.PresentationFragment}
 * {@link Attribute} is used to store necessary data/configuration which has to survive after a configuration changes or presentation style changes
 */
public class Attribute {
    @NonNull
    public Bundle onSaveInstanceState() {
        return new Bundle();
    }

    public void onRestoreInstanceState(@NonNull Bundle bundle) {}
}
