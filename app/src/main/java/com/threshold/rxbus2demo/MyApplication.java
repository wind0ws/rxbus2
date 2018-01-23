package com.threshold.rxbus2demo;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.threshold.rxbus2.RxBus;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * The Application for app.
 * Created by threshold on 2017/1/16.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("RxBus2")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG && super.isLoggable(priority, tag);
            }
        });

        //set MAIN THREAD for @RxSubscribe annotation
        RxBus.setMainScheduler(AndroidSchedulers.mainThread());
        //OR
        //        EventThread.setMainThreadScheduler(AndroidSchedulers.mainThread());


        //This option is optional. Using this only if you want to output RxBus log
//        RxBus.setLogger(new RxLogger());

//        RxBus.config(AndroidSchedulers.mainThread(),new RxLogger());//this method is removed in latest version.
    }
}
