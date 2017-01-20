# RxBus2
>This is like EventBus but use RxJava inside.

# Features

* Support annotation(RxSubscribe):auto register and unregister event listen.
* Support sticky event(Just like sticky broadcast).
* Support 3 type Bus: 
	* RxBus(Publish Bus)
	* BehaviorBus
	* ReplayBus

## Getting started
The first step is to include RxBus 2 into your project, for example, as a Gradle compile dependency:

Because of using [jitpack.io](https://jitpack.io/),so we need add the jitpack.io repository in your root project gradle:

```groovy
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```
and then add rxbus2 dependency in your module gradle:

```groovy
compile "com.github.wind0ws:rxbus2:1.0.0"
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

* register and unregister

```java
	@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_bus);
        RxBus.getInstance().register(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //auto release register with Annotation RxSubscribe.
        RxBus.getInstance().unregister(this);
    }
```

* post event

```java
public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFireEvent:
                RxBus.getInstance().post(100);
                RxBus.getInstance().post("Hi,Fire string event");
                break;
         }
 }

```
### Common usage

for example,ReplayBus.

```
ReplayBus.getInstance()
                .ofType(String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        String text = "{ [" + tag + "]:" + s+" }";
                        Log.d("ReplayBus”,text);
                    }
                });
  ReplayBus.getInstance().post("ReplayBus"+ RandomUtil.random(100));
```

## FAQ
* What is difference among of RxBus(PublishBus)/BehaviorBus/ReplayBus？

	Please see difference of PublishSubject / BehaviorSubject / ReplaySubject from RxJava doc.Here is link:<http://reactivex.io/documentation/subject.html>

* Can you demonstrate detailed?

	Please see app module in this repo,It show 3 type of bus usage.