package com.threshold.rxbus2demo;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.threshold.rxbus2.RxBus;
import com.threshold.rxbus2demo.util.RxLogger;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by threshold on 2017/1/16.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("ExpInquiry")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG && super.isLoggable(priority, tag);
            }
        });

        RxBus.config(AndroidSchedulers.mainThread(),new RxLogger());
        //If you don't want to output RxBus log,using this instead
//        RxBus.config(AndroidSchedulers.mainThread());
        //OR this
//        EventThread.setMainThreadScheduler(AndroidSchedulers.mainThread());
    }
}
