package com.threshold.rxbus2demo.util;

import com.threshold.rxbus2.util.Logger;

/**
 * Created by threshold on 2017/1/17.
 */

public class RxLogger implements Logger {

    @Override
    public void verbose(String message, Object... args) {
        com.orhanobut.logger.Logger.v(message, args);
    }

    @Override
    public void debug(Object msg) {
        com.orhanobut.logger.Logger.d(msg);
    }

    @Override
    public void debug(String message, Object... args) {
        com.orhanobut.logger.Logger.d(message, args);
    }

    @Override
    public void info(String message, Object... args) {
        com.orhanobut.logger.Logger.i(message, args);
    }

    @Override
    public void warning(String message, Object... args) {
        com.orhanobut.logger.Logger.w(message, args);
    }

    @Override
    public void error(String message, Object... args) {
        com.orhanobut.logger.Logger.e(message, args);
    }

    @Override
    public void error(Throwable throwable, String message, Object... args) {
        com.orhanobut.logger.Logger.e(throwable, message, args);
    }
}
