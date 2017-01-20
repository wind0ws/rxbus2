package com.threshold.rxbus2demo.util;

/**
 * Created by threshold on 2017/1/13.
 */

public enum RelayMode {
    /**
     * Will send last(or default item if has been set and no last item) and then.
     */
    BEHAVIOR("Behavior"),
    /**
     * Will send event from now on.
     */
    PUBLISH("Publish"),
    /**
     * Will send all event from beginning.
     */
    REPLAY("Replay");

    private String name;

    RelayMode(String name) {
        this.name = name;
    }

    public String getRelayModeName() {
        return name;
    }

}
