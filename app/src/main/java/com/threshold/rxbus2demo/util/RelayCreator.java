package com.threshold.rxbus2demo.util;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.jakewharton.rxrelay2.ReplayRelay;

/**
 * Created by threshold on 2017/1/13.
 */

public class RelayCreator {

    public static Relay<Object> create(RelayMode mode) {
        Relay<Object> relay;
        switch (mode) {
            case BEHAVIOR:
                relay = BehaviorRelay.create().toSerialized();
                break;
            case PUBLISH:
                relay = PublishRelay.create().toSerialized();
                break;
            case REPLAY:
                relay = ReplayRelay.create().toSerialized();
                break;
            default:
                throw new UnsupportedOperationException("unhandled the "+mode);
        }
        return relay;
    }

    public static Relay<Object> createBehavior(Object defaultItem) {
        return BehaviorRelay.createDefault(defaultItem).toSerialized();
    }

    public static Relay<Object> createReplay(int capacityHint) {
        return ReplayRelay.create(capacityHint).toSerialized();
    }



}
