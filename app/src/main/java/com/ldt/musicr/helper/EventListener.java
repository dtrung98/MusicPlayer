package com.ldt.musicr.helper;

public interface EventListener<T> {
    default boolean handleEvent(ReliableEvent<T> event) {
        return handleEvent(event, event.getReliable().getData());
    }

    boolean handleEvent(ReliableEvent<T> event, T data);
}
