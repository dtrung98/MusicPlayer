package com.ldt.musicr.interactors;

import androidx.annotation.NonNull;

public interface EventHandler<T> {
    default boolean shouldDelivery() {
        return true;
    }
    boolean handleEvent(@NonNull Event<T> result);
}
