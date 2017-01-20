package com.threshold.rxbus2;

import com.jakewharton.rxrelay2.Relay;
import com.threshold.rxbus2.util.EventThread;
import com.threshold.rxbus2.util.Logger;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * Base bus
 * Created by threshold on 2017/1/18.
 */

public class BaseBus implements Bus {

    private static Logger sLogger;

    /**
     * Set {@link EventThread#MAIN} {@link Scheduler} in your current environment.<br/>
     * <p>
     *     Handy method for {@link EventThread#setMainThreadScheduler(Scheduler)}
     * </p>
     * @param mainScheduler mainScheduler for {@link EventThread#MAIN}
     * @param logger Util for record RxBus log.if set null,will stop record log.
     */
    public static void config(Scheduler mainScheduler, Logger logger) {
        ObjectHelper.requireNonNull(mainScheduler, "mainScheduler == null ");
        EventThread.setMainThreadScheduler(mainScheduler);
        sLogger = logger;
    }

    /**
     * Set {@link EventThread#MAIN} {@link Scheduler} in your current environment.<br/>
     * <p>
     *     Handy method for {@link EventThread#setMainThreadScheduler(Scheduler)}
     * </p>
     * @param mainScheduler scheduler for {@link EventThread#MAIN}
     */
    public static void config(Scheduler mainScheduler) {
        config(mainScheduler,null);
    }

    private Relay<Object> relay;

    public BaseBus(Relay<Object> relay) {
        this.relay = relay.toSerialized();
    }

    @Override
    public void post(Object event) {
        ObjectHelper.requireNonNull(event, "event == null");
        if (hasObservers()) {
            relay.accept(event);
        }
    }

    @Override @SuppressWarnings("unchecked")
    public <T> Observable<T> ofType(Class<T> eventType) {
        if (eventType.equals(Object.class)) {
            return (Observable<T>) relay;
        }
        return relay.ofType(eventType);
    }

    @Override
    public boolean hasObservers() {
        return relay.hasObservers();
    }

    /**
     * Util for record log
     */
    static class LoggerUtil {

        private static boolean isLoggable() {
            return sLogger != null;
        }

        static void verbose(String message, Object... args) {
            if (isLoggable()) {
                sLogger.verbose(message, args);
            }
        }

        static void debug(Object msg) {
            if (isLoggable()) {
                sLogger.debug(msg);
            }
        }

        static void debug(String message, Object... args) {
            if (isLoggable()) {
                sLogger.debug(message, args);
            }
        }

        static void info(String message, Object... args) {
            if (isLoggable()) {
                sLogger.info(message, args);
            }
        }

        static void warning(String message, Object... args) {
            if (isLoggable()) {
                sLogger.warning(message, args);
            }
        }

        static void error(String message, Object... args) {
            if (isLoggable()) {
                sLogger.error(message, args);
            }
        }

        static void error(Throwable throwable, String message, Object... args) {
            if (isLoggable()) {
                sLogger.error(throwable, message, args);
            }
        }
    }
}
