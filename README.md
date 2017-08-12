# RxBus2
[![](https://jitpack.io/v/wind0ws/rxbus2.svg)](https://jitpack.io/#wind0ws/rxbus2)

>中文说明，请点[这里](http://www.jianshu.com/p/7f4a709d2be5)查看.

This is seems like [EventBus](https://github.com/greenrobot/EventBus) which intent for post and listen event but use RxJava2 inside.

# Features

* Support annotation(RxSubscribe):auto register and unregister event.
* Support sticky event(Just like sticky broadcast).
* Support 3 type Bus:
	* RxBus(Publish Bus)
	* BehaviorBus
	* ReplayBus

## [Getting started](https://jitpack.io/#wind0ws/rxbus2)
The first step is to include RxBus2 into your project, for example, as a Gradle compile dependency:

Because of using [jitpack.io](https://jitpack.io/),so we need add the jitpack.io repository in your root project gradle:

```groovy
allprojects {
 repositories {
    jcenter()
    //...some other repo.
    maven { url "https://jitpack.io" }
 }
}
```
and then add rxbus2 dependency in your module gradle:

```groovy
    implementation group: 'io.reactivex.rxjava2', name: 'rxjava', version: '2.x.x'
    implementation('com.jakewharton.rxrelay2:rxrelay:2.0.0'){
        exclude group: 'io.reactivex.rxjava2',module: 'rxjava'
    }
    implementation "com.github.wind0ws:rxbus2:1.1.0"
// maybe you need RxAndroid2 if you are using this on Android.
//   implementation('io.reactivex.rxjava2:rxandroid:2.x.x') {
//        exclude group: 'io.reactivex.rxjava2', module: 'rxjava'
//    }
//remember replace "2.x.x" to the latest version.
```
> seems complicated？
> Not at all. I just want to make your project using latest version of library and just one version. If you confused about it, just check the [gradle file](https://github.com/wind0ws/rxbus2/blob/master/app/build.gradle) on this repo.

> for gradle version below 3.0, just replace keyword ```implementation``` to ```compile```

We are done for integration.

Now we write the hello world app.

## Hello,World.
If you using this library on Android. Maybe you want to observe event on **Main Thread**(UI Thread).
So in your Application onCreate you should config MainScheduler for RxBus once.

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // if you using Annotation,and observeOnThread MAIN, you should config this.
        RxBus.setMainScheduler(AndroidSchedulers.mainThread());
    }
}
```
> You can find AndroidSchedulers here [RxAndroid](https://github.com/ReactiveX/RxAndroid)
### Annotation usage(just for RxBus)
* write listen event method

```java
    @RxSubscribe(observeOnThread = EventThread.MAIN)
    public void listenRxIntegerEvent(int code) {
        String text = String.format("{ Receive event: %s\nCurrent thread: %s }", code, Thread.currentThread());
        Log.d("RxBus",text)
    }
```
```java
    @RxSubscribe(observeOnThread = EventThread.IO,isSticky = true)
    public void listenRxStringEvent(String event) {
        final String text = String.format("{ Receive event: %s\nCurrent thread: %s }", event, Thread.currentThread());
        Log.d("RxBus",text);
    }
```

* register and unregister listen method

```java
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_bus);
	    //some other code ...
        RxBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        //auto release register with Annotation RxSubscribe.
        RxBus.getDefault().unregister(this);
        super.onDestroy();
    }
```

* post event

```java
@Override
public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFireEvent:
                RxBus.getDefault().post(100);//post integer event
                RxBus.getDefault().post("Hi,a string event");//post string event
		        RxBus.getDefault().post(new MyEvent("data on my event"));//post my event.
                break;
         }
 }

```
### Common usage

for example,ReplayBus.

```java
ReplayBus.getDefault()
                .ofType(String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        String text = "{ [" + tag + "]:" + s+" }";
                        Log.d("ReplayBus”,text);
                    }
                });
  ReplayBus.getDefault().post("ReplayBus"+ RandomUtil.random(100));
```

### Proguard
add this line into your "proguard-rules.pro" file.
>If you use annotation, you need it. Don't forget keep your bean or entity in proguard too.

```groovy
# For using annotation
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepclassmembers class ** {
    @com.threshold.rxbus2.annotation.RxSubscribe <methods>;
}
-keep enum com.threshold.rxbus2.util.EventThread { *; }
```

## FAQ
* What is difference among of RxBus(PublishBus)/BehaviorBus/ReplayBus？

	Please see difference of PublishSubject / BehaviorSubject / ReplaySubject from RxJava doc.Here is link:<http://reactivex.io/documentation/subject.html>

* Can you demonstrate detailed?

	Please see app module in this repo,It show 3 type bus usage.