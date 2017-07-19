package com.huier.fw_rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * RxJava有四个基本概念：Observable(可观察者，即被观察者)、Observer(观察者)、subscribe(订阅)、事件。
 * Observer通过subscribe()方法实现订阅关系，从而Observable可以在需要的时候发出事件来通知Observer。
 * 与传统观察者模式不同，RxJava的事件回调方法除了普通事件onNext()(相当于onClick()/onEvent())外，
 * 还定义了两个特殊的事件：onCompleted()和onError()。
 * (1)onCompleted():事件队列完结。RxJava不仅把每个事件单独处理，还会把它们看做一个队列。RxJava规定，
 *    当不会再有新的onNext()发出时，需要触发onCompleted()方法作为标志。
 * (2)onError(): 事件队列异常。在事件处理过程中出异常时，onError()会被触发，同时队列自动终止，不允许再有事件发出。
 * (3)在一个正确运行的事件序列中, onCompleted() 和 onError() 有且只有一个，并且是事件序列中的最后一个。需要注意的是，
 *    onCompleted() 和 onError() 二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个。
 */
public class RxJava1Activity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "tag";
    private Button btnRxJava1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        btnRxJava1 = (Button)findViewById(R.id.btn_rxjava1);
        btnRxJava1.setOnClickListener(this);
    }

    /** 观察者订阅被观察者 **/
    private void subscribe(){
        /**
         * subscribe()有多个重载的方法：
         * (1)不带任何参数的subscribe()表示下游不关心任何事件,你上游尽管发你的数据去吧,老子可不管你发什么；
         * (2)带有一个Consumer参数的方法表示下游只关心onNext事件,其他的事件我假装没看见;
         * 如下使用的是带有Observer参数
         */
        mObservable1.subscribe(mObserver);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_rxjava1:
                subscribe();
                break;
        }
    }

    /** 创建Observer观察者 **/
    Observer<String> mObserver = new Observer<String>() {
        /**
         * Disposable：对应于水管（被观察者属于下游，观察者属于下游）, 我们可以把它理解成两根管道之间的一个机关,
         * 当调用它的dispose()方法时, 它就会将两根管道切断, 从而导致下游收不到事件。
         *
         */
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG,"Observer onSubscribe");
        }

        @Override
        public void onNext(@NonNull String s) {
            Log.d(TAG,"Observer onNext");
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG,"Observer onError");
        }

        @Override
        public void onComplete() {
            Log.d(TAG,"Observer onComplete");
        }
    };

    /**
     * ?在第2版中不知道如何订阅
     * 除了Observer接口之外，RxJava还内置了一个实现了Observer的抽象类：Subscriber。
     * Subscriber对Observer接口进行了一些扩展，但他们的基本使用方式是完全一样的。
     * 不仅基本使用方式一样，实质上，在RxJava的subscribe过程中，Observer也总是会先被转换成一个Subscriber再使用。
     */
//    Subscriber<String> mSubscriber = new Subscriber<String>() {
//        @Override
//        public void onSubscribe(Subscription s) {
//            Log.d(TAG,"Subscriber onSubscribe");
//        }
//
//        @Override
//        public void onNext(String s) {
//            Log.d(TAG,"Subscriber onNext");
//        }
//
//        @Override
//        public void onError(Throwable t) {
//            Log.d(TAG,"Subscriber onError");
//        }
//
//        @Override
//        public void onComplete() {
//            Log.d(TAG,"Subscriber onComplete");
//        }
//    };

    /**
     * 创建Observable被观察者
     * Observable即被观察者，它决定什么时候触发事件以及触发怎样的事件。RxJava使用create()方法来创建一
     * 个Observable，并为它定义事件触发规则。
     * create传入了一个ObservableOnSubscribe对象作为参数，ObservableOnSubscribe会被存储在返回的Observable对象中，
     * 它的作用相当于一个计划表，当Observable被订阅的时候，ObservableOnSubscribe的subscribe()方法会自动被调用，
     * 事件序列就会依照设定依次触发(对于上面的代码，就是观察者ObservableEmitter将会被调用三次onNext()和一次onCompleted())。
     * 这样，由被观察者调用了观察者的回调方法，就实现了由被观察者向观察者的事件传递，即观察者模式。
     * create()方法是RxJava最基本的创造事件序列的方法。
     *
     */
    Observable mObservable1 = Observable.create(new ObservableOnSubscribe() {
        /**
         * ObservableEmitter：这个就是用来发出事件的，它可以发出三种类型的事件，通过调用emitter的onNext(T value)、onComplete()
         * 和onError(Throwable error)就可以分别发出next事件、complete事件和error事件。
         * 但是，请注意，并不意味着你可以随意乱七八糟发射事件，需要满足一定的规则：
         * (1)被观察者可以发送无限个onNext, 观察者也可以接收无限个onNext；
         * (2)当被观察者发送了一个onComplete后, 被观察者onComplete之后的事件将会继续发送, 而观察者收到onComplete事件之后将不再继续接收事件；
         * (3)当被观察者发送了一个onError后, 被观察者onError之后的事件将继续发送, 而观察者收到onError事件之后将不再继续接收事件；
         * (4)被观察者可以不发送onComplete或onError；
         * (5)最为关键的是onComplete和onError必须唯一并且互斥, 即不能发多个onComplete, 也不能发多个onError,
         *    也不能先发一个onComplete, 然后再发一个onError, 反之亦然。
         */
        @Override
        public void subscribe(@NonNull ObservableEmitter e) throws Exception {
            Log.d(TAG,"Observable subscribe");
            e.onNext("hello");
            e.onNext("Hi");
            e.onNext("Aloha");
            e.onComplete();
        }
    });
    /**
     * just(T...):将传入的参数依次发送出来
     * 将会依次调用：
     * onNext("Hello");
     * onNext("Hi");
     * onNext("Aloha");
     * onCompleted();
     * 都和之前的create()是等价的
     */
    Observable mObservable2 = Observable.just("Hello", "Hi", "Aloha");

    String[] words = {"Hello", "Hi", "Aloha"};
    /**
     * 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来。
     * 将会依次调用：
     * onNext("Hello");
     * onNext("Hi");
     * onNext("Aloha");
     * onCompleted();
     * 都和之前的create()是等价的
     */
    Observable observable3 = Observable.fromArray(words);
}
