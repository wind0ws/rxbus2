package com.threshold.rxbus2;

import com.jakewharton.rxrelay2.ReplayRelay;

/**
 * Replays events to Observers.<br>
 * Every observer could receive same sequence event.
 * See also {@link ReplayRelay}
 */
@SuppressWarnings("WeakerAccess")
public class ReplayBus extends BaseBus {

    private static volatile ReplayBus defaultBus;

    public static ReplayBus getDefault() {
        if (defaultBus == null) {
            synchronized (ReplayBus.class) {
                if (defaultBus == null) {
                    defaultBus = new ReplayBus();
                }
            }
        }
        return defaultBus;
    }

    public ReplayBus(ReplayRelay<Object> replayRelay) {
        super(replayRelay);
    }

    public ReplayBus() {
        this(ReplayRelay.create());
    }

}
