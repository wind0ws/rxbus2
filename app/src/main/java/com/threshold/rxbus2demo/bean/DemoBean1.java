package com.threshold.rxbus2demo.bean;

/**
 * DemoBean1
 * Created by threshold on 2018/1/24.
 */

public class DemoBean1 {

    public DemoBean1() {
    }

    public DemoBean1(String data) {
        this.data = data;
    }

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DemoBean1{" +
                "data='" + data + '\'' +
                '}';
    }
}
