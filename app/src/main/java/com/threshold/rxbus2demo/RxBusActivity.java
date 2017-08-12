package com.threshold.rxbus2demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.threshold.rxbus2.RxBus;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;
import com.threshold.rxbus2demo.util.RandomUtil;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by threshold on 2017/1/18.
 */

public class RxBusActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("RxBus");
        setContentView(R.layout.activity_rx_bus);

        textView = findViewById(R.id.text);
        //clear sticky event if you need.
//        RxBus.getDefault().clearSticky();
        //auto register listen event.
        RxBus.getDefault().register(this);
        //manual listen event
//        Disposable subscribe = RxBus.getDefault()
//                .ofStickyType(String.class)
//                .observeOn(Schedulers.io())
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        manualListenRxEvent("First Subscriber",s);
//                    }
//                });
//        mCompositeDisposable.add(subscribe);
        Logger.d("Is register Success:%s", RxBus.getDefault().hasObservers());
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN) @SuppressWarnings("unused")
    public void autoListenRxEvent(int code) {
        String text = String.format("{ Receive event: %s\nCurrent thread: %s }", code, Thread.currentThread());
        Logger.d(text);
        textView.append(text);
        textView.append("\n");
    }

    @RxSubscribe(observeOnThread = EventThread.IO,isSticky = true) @SuppressWarnings("unused")
    public void autoListenRxEvent2(String event) {
        final String text = String.format("{ Receive event: %s\nCurrent thread: %s }", event, Thread.currentThread());
        Logger.d(text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(text);
                textView.append("\n");
            }
        });
    }

    public void manualListenRxEvent(String id, String event) {
        final String text = String.format("{[%s Receive event]: %s}", id, event);
        Logger.d(text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(text);
                textView.append("\n");
            }
        });
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFireEvent:
                RxBus.getDefault().post(RandomUtil.random(10));
                RxBus.getDefault().post("Hi, event "+RandomUtil.random(10));
                break;
            case R.id.btnFireStickyEvent:
                RxBus.getDefault().postSticky("Hello, sticky event "+RandomUtil.random(100));
                break;
            case R.id.btnAddNewSubscriber:
                Disposable subscribe = RxBus.getDefault()
                        .ofStickyType(String.class)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                manualListenRxEvent("Second Subscriber", s);
                            }
                        });
                mCompositeDisposable.add(subscribe);
                view.setEnabled(false);
                break;
            case R.id.btnRemoveStickyEvent:
                List<String> sticky = RxBus.getDefault()
                        .getSticky(String.class);
                if (sticky != null && sticky.size() > 0) {
                    RxBus.getDefault().removeSticky(sticky.get(0));
                }
                break;
        }

//        List<Integer> integers = new ArrayList<>();
//        integers.add(1);
//        integers.add(2);
//        integers.add(3);
//        RxBus.getDefault().post(integers);
//        RxBus.getDefault().post(new String[]{"Str1","Str2","Str3"});
//        RxBus.getDefault().post(new int[]{1,2,3});
//        RxBus.getDefault().post('C');
//        RxBus.getDefault().post(0.05f);
//        RxBus.getDefault().post(13.56);
//        RxBus.getDefault().post(Long.MAX_VALUE);
//        RxBus.getDefault().post(Byte.MIN_VALUE);
//        RxBus.getDefault().post(Boolean.FALSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // manual listen event should release by yourself.
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
        //auto release register with Annotation RxSubscribe.
        RxBus.getDefault().unregister(this);
    }

}
