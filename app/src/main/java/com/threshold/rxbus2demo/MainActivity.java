package com.threshold.rxbus2demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRxBus:
                startActivity(RxBusActivity.class);
                break;
            case R.id.btnBehaviorBus:
                startActivity(BehaviorBusActivity.class);
                break;
            case R.id.btnReplayBus:
                startActivity(ReplayBusActivity.class);
                break;
        }
    }

    private void startActivity(Class<?> activity) {
        startActivity(new Intent(this,activity));
    }
}
