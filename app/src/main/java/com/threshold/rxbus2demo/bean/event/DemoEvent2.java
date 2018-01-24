package com.threshold.rxbus2demo.bean.event;

import android.support.annotation.NonNull;

import com.threshold.rxbus2demo.bean.DemoBean2;

/**
 * DemoEvent2, which hold data of {@link DemoEvent2}
 * Created by threshold on 2018/1/24.
 */

public class DemoEvent2 extends RxEvent {

    private DemoBean2 mDemoBean2;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DemoEvent2(@NonNull Object source,@NonNull DemoBean2 demoBean2) {
        super(source);
        mDemoBean2 = demoBean2;
    }

    @NonNull
    public DemoBean2 getDemoBean2() {
        return mDemoBean2;
    }
}
