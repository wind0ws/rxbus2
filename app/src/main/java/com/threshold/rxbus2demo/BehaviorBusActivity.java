package com.threshold.rxbus2demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.threshold.rxbus2.BehaviorBus;
import com.threshold.rxbus2demo.util.RandomUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by threshold on 2017/1/18.
 */

public class BehaviorBusActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    BehaviorBus behaviorBus;
    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("BehaviorBus");
        setContentView(R.layout.activity_behavior_bus);
        textView = (TextView) findViewById(R.id.text);
        behaviorBus =  new BehaviorBus("Default Item");
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddFirstSubscription:
                listenStringEvent("First subscription");
                v.setEnabled(false);
                break;
            case R.id.btnAddSecondSubscription:
                listenStringEvent("Second subscription");
                v.setEnabled(false);
                break;
            case R.id.btnFireEvent:
                behaviorBus.post("Hello"+ RandomUtil.random(100));
                break;
        }
    }

    private void listenStringEvent(final String tag) {
        Disposable subscribe = behaviorBus.ofType(String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        String text = "{ [" + tag + "]:" + s+" }";
                        textView.append(text);
                        textView.append("\n");
                        Logger.d(text);
                    }
                });
        compositeDisposable.add(subscribe);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
