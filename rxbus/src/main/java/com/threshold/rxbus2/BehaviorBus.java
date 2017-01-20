package com.threshold.rxbus2;

import com.jakewharton.rxrelay2.BehaviorRelay;

/**
 * Relay that emits the most recent item it has observed and all subsequent observed items to each subscribed.
 * See also {@link BehaviorRelay}
 */

public class BehaviorBus extends BaseBus {

    private static volatile BehaviorBus defaultBus;

    public static BehaviorBus getInstance() {
        if (defaultBus == null) {
            synchronized (BehaviorBus.class) {
                if (defaultBus == null) {
                    defaultBus = new BehaviorBus();
                }
            }
        }
        return defaultBus;
    }

    public BehaviorBus() {
        this(BehaviorRelay.create());
    }

    public BehaviorBus(Object defaultItem) {
        this(BehaviorRelay.createDefault(defaultItem));
    }

    public BehaviorBus(BehaviorRelay<Object> behaviorRelay) {
        super(behaviorRelay);
    }
}
