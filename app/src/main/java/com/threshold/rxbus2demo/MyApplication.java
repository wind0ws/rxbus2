package com.threshold.rxbus2demo;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
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

        Logger.init("RxBusDemo")
                .hideThreadInfo()
                .methodCount(3)
                .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE);

        RxBus.config(AndroidSchedulers.mainThread(),new RxLogger());
        //If you don't want to record RxBus log,using this
//        RxBus.config(AndroidSchedulers.mainThread());

//        EventThread.setMainThreadScheduler(AndroidSchedulers.mainThread());
    }
}
