package com.threshold.rxbus2.util;

/**
 * Created by threshold on 2017/1/16.
 */

public interface Logger {
    void verbose(String message, Object... args);
    void debug(Object msg);
    void debug(String message, Object... args);
    void info(String message, Object... args);
    void warning(String message, Object... args);
    void error(String message, Object... args);
    void error(Throwable throwable, String message, Object... args);
}
