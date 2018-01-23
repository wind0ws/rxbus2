package com.threshold.rxbus2;

import com.jakewharton.rxrelay2.Relay;
import com.threshold.rxbus2.util.EventThread;
import com.threshold.rxbus2.util.Logger;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * Base bus
 * Created by threshold on 2017/1/18.
 */

public class BaseBus implements Bus {

    private static Logger sLogger;

    /*
     * Set {@link EventThread#MAIN} {@link Scheduler} in your current environment.<br/>
     * <p>
     *     Handy method for {@link EventThread#setMainThreadScheduler(Scheduler)}
     * </p>
     * This is only need to be set once.
     * @param mainScheduler mainScheduler for {@link EventThread#MAIN}
     * @param logger Util for record Bus log.if set null, no log will output.
     */
//    public static void config(@NonNull Scheduler mainScheduler,@Nullable Logger logger) {
//        EventThread.setMainThreadScheduler(ObjectHelper.requireNonNull(mainScheduler, "mainScheduler == null "));
//        sLogger = logger;
//    }

    /**
     * Util for record Bus log. If set null, no log will output.
     * @param logger {@link Logger}
     */
    public static void setLogger(@Nullable Logger logger) {
        sLogger = logger;
    }

    /**
     * Set {@link EventThread#MAIN} {@link Scheduler} in your current environment.<br/>
     * For example in Android,you probably set @{code AndroidSchedulers.mainThread()}.
     * <p>
     *     Handy method for {@link EventThread#setMainThreadScheduler(Scheduler)}
     * </p>
     * @param mainScheduler mainScheduler for {@link EventThread#MAIN}
     */
    public static void setMainScheduler(@NonNull Scheduler mainScheduler) {
        EventThread.setMainThreadScheduler(mainScheduler);
    }

    private Relay<Object> relay;

    public BaseBus(Relay<Object> relay) {
        this.relay = relay.toSerialized();
    }

    @Override
    public void post(@NonNull Object event) {
        ObjectHelper.requireNonNull(event, "event == null");
        if (hasObservers()) {
            LoggerUtil.debug("post event: %s", event);
            relay.accept(event);
        } else {
            LoggerUtil.warning("no observers,event will be discard:%s",event);
        }
    }

    @Override @SuppressWarnings("unchecked")
    public <T> Observable<T> ofType(@NonNull Class<T> eventType) {
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
