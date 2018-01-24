package com.threshold.rxbus2demo.bean;

/**
 * DemoBean2
 * Created by threshold on 2018/1/24.
 */

public class DemoBean2 {

    public DemoBean2() {
    }

    public DemoBean2(Object data) {
        this.data = data;
    }

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DemoBean2{" +
                "data=" + data +
                '}';
    }
}
