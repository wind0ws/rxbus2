package com.threshold.rxbus2;

import com.jakewharton.rxrelay2.PublishRelay;
import com.threshold.rxbus2.annotation.RxSubscribe;
import com.threshold.rxbus2.util.EventThread;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.schedulers.Schedulers;

/**
 * once an {@link Observer} has subscribed, emits all subsequently observed items to the
 * subscriber.<br/>
 * See also {@link PublishRelay}
 */
public class RxBus extends BaseBus {

    private static volatile RxBus defaultBus;

    private Map<Object, CompositeDisposable> subscriptions = new HashMap<>();
    private final Map<Class<?>, List<Object>> stickyEventMap;

    /**
     * Get the default instance of RxBus
     * @return instance of RxBus
     */
    public static RxBus getInstance() {
        if (defaultBus == null) {
            synchronized (RxBus.class) {
                if (defaultBus == null) {
                    defaultBus = new RxBus();
                }
            }
        }
        return defaultBus;
    }

    public RxBus(PublishRelay<Object> publishRelay) {
        super(publishRelay);
        stickyEventMap = new ConcurrentHashMap<>();
    }

    /**
     * Default constructor,use {@link PublishRelay} for internal bus.
     */
    public RxBus() {
        this(PublishRelay.create());
    }

    /**
     * Fire a sticky event.
     * @param event sticky event.
     */
    public void postSticky(Object event) {
        ObjectHelper.requireNonNull(event, "event == null");
        synchronized (stickyEventMap) {
            List<Object> stickyEvents = stickyEventMap.get(event.getClass());
            boolean isStickEventListInMap = true;
            if (stickyEvents == null) {
                stickyEvents = new ArrayList<>();
                isStickEventListInMap = false;
            }
            stickyEvents.add(event);
            if (!isStickEventListInMap) {
                stickyEventMap.put(event.getClass(), stickyEvents);
            }
        }
        post(event);
    }

    /**
     * Get list of specific type sticky event.
     *
     * <p> DO NOT ALTER (ADD REMOVE) THIS STICKY EVENT LIST! <br/>
     *
     * If you want delete some of this list,please use {@link #removeSticky(Class)} or {@link #removeSticky(Object)} or {@link #removeAllSticky()} <br/>
     * If you want add some sticky event,please use {@link #postSticky(Object)}
     * @param eventType type of T
     * @param <T> the sticky event type that you want
     * @return list of specific stick event
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getSticky(Class<T> eventType) {
        synchronized (stickyEventMap) {
            return (List<T>) stickyEventMap.get(eventType);
        }
    }

    /**
     * Remove specific sticky event
     * @param event the sticky event that you want remove
     */
    public void removeSticky(Object event) {
        ObjectHelper.requireNonNull(event, "event == null");
        synchronized (stickyEventMap) {
            List<Object> stickyEvents = stickyEventMap.get(event.getClass());
            if (stickyEvents != null) {
                stickyEvents.remove(event);
            }
        }
    }

    /**
     * Remove specific type sticky event
     * @param eventType the sticky event type that you want remove
     */
    public void removeSticky(Class<?> eventType) {
        synchronized (stickyEventMap) {
            stickyEventMap.remove(eventType);
        }
    }

    /**
     * Remove all sticky event
     */
    public void clearSticky() {
        synchronized (stickyEventMap) {
            stickyEventMap.clear();
        }
    }

    /**
     * Get the specific type sticky event observable
     * @param eventType the sticky event type that you want listen
     * @param <T> event type
     * @return Observable of {@code T}
     */
    public <T> Observable<T> ofStickyType(Class<T> eventType) {
        synchronized (stickyEventMap) {
            @SuppressWarnings("unchecked")
            List<T> stickyEvents = (List<T>) stickyEventMap.get(eventType);
            if (stickyEvents != null && stickyEvents.size() > 0) {
                return Observable.fromIterable(stickyEvents)
                        .mergeWith(ofType(eventType));
            }
        }
        return ofType(eventType);
    }

    /**
     * unSubscribe all registered event and clear all sticky event.
     */
    public void reset() {
        Observable.fromIterable(subscriptions.values())
                .filter(new Predicate<CompositeDisposable>() {
                    @Override
                    public boolean test(CompositeDisposable compositeDisposable) throws Exception {
                        return compositeDisposable!=null&&!compositeDisposable.isDisposed();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<CompositeDisposable>() {
                    @Override
                    public void accept(CompositeDisposable compositeDisposable) throws Exception {
                        compositeDisposable.clear();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LoggerUtil.error(throwable, "Dispose subscription");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        stickyEventMap.clear();
                        subscriptions.clear();
                    }
                });
    }

    /**
     * Indicate {@code subscriber} is registered.
     * @param subscriber subscriber to subscribe event
     * @return true for registered
     */
    public synchronized boolean isRegistered(Object subscriber) {
        return subscriber!=null&&subscriptions.containsKey(subscriber.hashCode());
    }

    /**
     * register with {@link RxSubscribe} annotation method
     * @param subscriber the instance of class that you want to find {@link RxSubscribe} annotation method
     */
    public void register(final Object subscriber) {
        Observable.just(subscriber)
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object obj) throws Exception {
                        boolean registered = isRegistered(obj);
                        if (registered) {
                            LoggerUtil.warning("%s has already registered",obj);
                        }
                        return !registered;
                    }
                })
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object obj) throws Exception {
                        LoggerUtil.debug("start to analyze subscriber: %s", obj);
                    }
                })
                .flatMap(new Function<Object, ObservableSource<Method>>() {
                    @Override
                    public ObservableSource<Method> apply(Object obj) throws Exception {
                        return Observable.fromArray(obj.getClass().getDeclaredMethods());
                    }
                })
                .map(new Function<Method, Method>() {
                    @Override
                    public Method apply(Method method) throws Exception {
                        LoggerUtil.debug("Set method can accessible: %s ", method);
                        method.setAccessible(true);
                        return method;
                    }
                })
                .filter(new Predicate<Method>() {
                    @Override
                    public boolean test(Method method) throws Exception {
                        boolean isOK = method.isAnnotationPresent(RxSubscribe.class) && method.getParameterTypes() != null && method.getParameterTypes().length > 0;
                        LoggerUtil.debug("%s is has RxSubscribe annotation: %s", method, isOK);
                        return isOK;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Method>() {
                    @Override
                    public void accept(Method method) throws Exception {
                        LoggerUtil.debug("now start add subscription method: %s", method);
                        addSubscriptionMethod(subscriber, method);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LoggerUtil.error(throwable, "%s fail register", subscriber);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        LoggerUtil.debug("%s register complete", subscriber);
                    }
                });
    }

    private void addSubscriptionMethod(final Object subscriber, final Method method) {
        Disposable subscribe =
                Observable.just(method.getParameterTypes()[0])
                        .doOnNext(new Consumer<Class<?>>() {
                            @Override
                            public void accept(Class<?> type) throws Exception {
                                LoggerUtil.debug("Origin: [method: %s ] , param[0] type: %s", method, type);
                            }
                        })
                        .map(new Function<Class<?>, Class<?>>() {
                            @Override
                            public Class<?> apply(Class<?> type) throws Exception {
                                return boxPrimitiveType(type);
                            }
                        })
                        .doOnNext(new Consumer<Class<?>>() {
                            @Override
                            public void accept(Class<?> type) throws Exception {
                                LoggerUtil.debug("Listen event type: %s", type);
                            }
                        })
                        .flatMap(new Function<Class<?>, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Class<?> type) throws Exception {
                                RxSubscribe rxAnnotation = method.getAnnotation(RxSubscribe.class);
                                LoggerUtil.debug("%s RxSubscribe Annotation: %s", method, rxAnnotation.observeOnThread());
                                Observable<?> observable = rxAnnotation.isSticky() ? ofStickyType(type) : ofType(type);
                                observable.observeOn(EventThread.getScheduler(rxAnnotation.observeOnThread()));
                                return observable;
                            }
                        })
                        .subscribe(
                                new Consumer<Object>() {
                                    @Override
                                    @SuppressWarnings("all")
                                    public void accept(Object obj) throws Exception {
                                        LoggerUtil.debug("Subscriber:%s invoke Method:%s", subscriber, method);
                                        try {
                                            method.invoke(subscriber, obj);
                                        } catch (IllegalAccessException e) {
                                            LoggerUtil.error(e, "%s invoke error", method);
                                        } catch (InvocationTargetException e) {
                                            LoggerUtil.error(e, "%s invoke error", method);
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        LoggerUtil.error(throwable, "%s can't invoke %s", subscriber, method);
                                    }
                                });
        CompositeDisposable compositeDisposable = subscriptions.get(subscriber.hashCode());
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(subscribe);
        subscriptions.put(subscriber.hashCode(), compositeDisposable);
        LoggerUtil.debug("Registered %s", method);
    }

    /**
     * unregister {@link RxSubscribe} annotation method
     * @param subscriber the instance with {@link RxSubscribe} annotation method.
     */
    public void unregister(final Object subscriber) {
        Flowable.just(subscriber)
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object obj) throws Exception {
                        return obj != null;
                    }
                })
                .map(new Function<Object, CompositeDisposable>() {
                    @Override
                    public CompositeDisposable apply(Object subscriber) throws Exception {
                        return subscriptions.get(subscriber.hashCode());
                    }
                })
                .filter(new Predicate<CompositeDisposable>() {
                    @Override
                    public boolean test(CompositeDisposable compositeDisposable) throws Exception {
                        return compositeDisposable != null && !compositeDisposable.isDisposed();
                    }
                })
                .subscribe(new Subscriber<CompositeDisposable>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(CompositeDisposable compositeDisposable) {
                        compositeDisposable.dispose();
                        subscriptions.remove(subscriber.hashCode());
                        LoggerUtil.debug("remove subscription of %s", subscriber);
                    }

                    @Override
                    public void onError(Throwable t) {
                        LoggerUtil.error(t, "%s unregister RxBus", subscriber);
                    }

                    @Override
                    public void onComplete() {
                        LoggerUtil.debug("%s unregister RxBus completed!", subscriber);
                    }
                });
    }

    private Class<?> boxPrimitiveType(Class<?> cls) {
        String clsName = cls.getName();
        if (clsName.equals(int.class.getName())) {
            cls = Integer.class;
        } else if (clsName.equals(double.class.getName())) {
            cls = Double.class;
        } else if (clsName.equals(float.class.getName())) {
            cls = Float.class;
        } else if (clsName.equals(long.class.getName())) {
            cls = Long.class;
        } else if (clsName.equals(byte.class.getName())) {
            cls = Byte.class;
        } else if (clsName.equals(short.class.getName())) {
            cls = Short.class;
        } else if (clsName.equals(boolean.class.getName())) {
            cls = Boolean.class;
        } else if (clsName.equals(char.class.getName())) {
            cls = Character.class;
        }
        return cls;
    }

}
