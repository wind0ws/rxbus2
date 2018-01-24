package com.threshold.rxbus2demo.bean.event;

import android.support.annotation.NonNull;

import com.threshold.rxbus2demo.bean.DemoBean1;

/**
 * A Event that bring with a {@link DemoEvent1}
 * Created by threshold on 2018/1/24.
 */

public class DemoEvent1 extends RxEvent {

    private DemoBean1 mDemoBean1;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DemoEvent1(@NonNull Object source, @NonNull DemoBean1 demoBean1) {
        super(source);
        this.mDemoBean1 = demoBean1;
    }

    @NonNull
    public DemoBean1 getDemoBean1() {
        return mDemoBean1;
    }
}
