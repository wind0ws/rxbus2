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

        textView = (TextView) findViewById(R.id.text);
        //auto register listen event.
        RxBus.getInstance().register(this);
        //manual listen event
//        Disposable subscribe = RxBus.getInstance()
//                .ofStickyType(String.class)
//                .observeOn(Schedulers.io())
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        listenRxEvent("First Subscriber",s);
//                    }
//                });
//        mCompositeDisposable.add(subscribe);
        Logger.d("Is register Success:%s", RxBus.getInstance().hasObservers());
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN) @SuppressWarnings("unused")
    public void listenRxEvent(int code) {
        String text = String.format("{ Receive event: %s\nCurrent thread: %s }", code, Thread.currentThread());
        Logger.d(text);
        textView.append(text);
        textView.append("\n");
    }

    @RxSubscribe(observeOnThread = EventThread.IO,isSticky = true) @SuppressWarnings("unused")
    public void listenRxEvent2(String event) {
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

    public void listenRxEvent(String id, String event) {
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
                RxBus.getInstance().post(RandomUtil.random(10));
                RxBus.getInstance().post("Hi,Fire event "+RandomUtil.random(10));
                break;
            case R.id.btnFireStickyEvent:
                RxBus.getInstance().postSticky("Hello ,Fire sticky event "+RandomUtil.random(100));
                break;
            case R.id.btnAddNewSubscriber:
                Disposable subscribe = RxBus.getInstance()
                        .ofStickyType(String.class)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                listenRxEvent("Second Subscriber", s);
                            }
                        });
                mCompositeDisposable.add(subscribe);
                view.setEnabled(false);
                break;
            case R.id.btnRemoveStickyEvent:
                List<String> sticky = RxBus.getInstance()
                        .getSticky(String.class);
                if (sticky != null && sticky.size() > 0) {
                    RxBus.getInstance().removeSticky(sticky.get(0));
                }
                break;
        }

//        List<Integer> integers = new ArrayList<>();
//        integers.add(1);
//        integers.add(2);
//        integers.add(3);
//        RxBus.getInstance().post(integers);
//        RxBus.getInstance().post(new String[]{"Str1","Str2","Str3"});
//        RxBus.getInstance().post(new int[]{1,2,3});
//        RxBus.getInstance().post('C');
//        RxBus.getInstance().post(0.05f);
//        RxBus.getInstance().post(13.56);
//        RxBus.getInstance().post(Long.MAX_VALUE);
//        RxBus.getInstance().post(Byte.MIN_VALUE);
//        RxBus.getInstance().post(Boolean.FALSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // manual listen event should release by yourself.
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
        //auto release register with Annotation RxSubscribe.
        RxBus.getInstance().unregister(this);
    }

}
