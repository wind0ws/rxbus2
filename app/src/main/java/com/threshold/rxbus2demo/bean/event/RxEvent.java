package com.threshold.rxbus2demo.bean.event;

import android.support.annotation.NonNull;

import java.util.EventObject;

/**
 * All event send by RxBus2 should derived this class.
 * Created by threshold on 2018/1/24.
 */
@SuppressWarnings("WeakerAccess")
public abstract class RxEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public RxEvent(@NonNull Object source) {
        super(source);
    }

    @Override
    public String toString() {
        return "RxEvent{" +
                "source=" + source +
                '}';
    }
}
