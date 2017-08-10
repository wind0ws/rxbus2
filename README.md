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
The first step is to include RxBus 2 into your project, for example, as a Gradle compile dependency:

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
implementation "com.github.wind0ws:rxbus2:1.0.1"
```
> for gradle version below 3.0, add dependency like this:
>
```groovy
 compile "com.github.wind0ws:rxbus2:1.0.1"
```

We are done for integration.

Now we write the hello world app.

## Hello,World.
If you using this library on Android.Maybe you want to observe event on **Main Thread**(UI Thread).
So in your Application onCreate you should config MainScheduler for RxBus(You can find AndroidSchedulers on [RxAndroid](https://github.com/ReactiveX/RxAndroid)) once.

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RxBus.config(AndroidSchedulers.mainThread());
    }
}
```
### Annotation usage(for RxBus)
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
        RxBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //auto release register with Annotation RxSubscribe.
        RxBus.getDefault().unregister(this);
    }
```

* post event

```java
public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFireEvent:
                RxBus.getDefault().post(100);
                RxBus.getDefault().post("Hi,Fire string event");
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
>If you use annotation, you need it. Don't forget keep your bean or entity in proguard.

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