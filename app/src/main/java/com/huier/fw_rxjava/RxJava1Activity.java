package com.huier.fw_rxjava;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * 基本使用，参考：http://www.jianshu.com/p/d149043d103a
 * RxJava有四个基本概念：Observable(即被观察者)、Observer(观察者)、subscribe(订阅)、事件。
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
    private Button btnRxJava1;
    private Button btnRxJava2;
    private Button btnRxJava3;
    private Button btnRxJava4;
    private Button btnRxJava5;
    private Button btnRxJava6;

    public static void entry(Context from){
        Intent intent = new Intent(from,RxJava1Activity.class);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java1);
        initView();
    }

    private void initView(){
        btnRxJava1 = (Button)findViewById(R.id.btn_rxjava1);
        btnRxJava1.setOnClickListener(this);
        btnRxJava2 = (Button)findViewById(R.id.btn_rxjava2);
        btnRxJava2.setOnClickListener(this);
        btnRxJava3 = (Button)findViewById(R.id.btn_rxjava3);
        btnRxJava3.setOnClickListener(this);
        btnRxJava4 = (Button)findViewById(R.id.btn_rxjava4);
        btnRxJava4.setOnClickListener(this);
        btnRxJava5 = (Button)findViewById(R.id.btn_rxjava5);
        btnRxJava5.setOnClickListener(this);
        btnRxJava6 = (Button)findViewById(R.id.btn_rxjava6);
        btnRxJava6.setOnClickListener(this);
    }

    /** 观察者订阅被观察者 **/
    private void subscribe(int id){
        Observable observable;
        switch (id){
            case R.id.btn_rxjava1:
                /**
                 * subscribe()有多个重载的方法：
                 * (1)不带任何参数的subscribe()表示下游不关心任何事件,你上游尽管发你的数据去吧,老子可不管你发什么；
                 * (2)带有一个Consumer参数的方法表示下游只关心onNext事件,其他的事件我假装没看见;
                 * (3)如下使用的是带有Observer参数
                 */
                observable = createObservable1();
                observable.subscribe(mObserver);
                break;
            case R.id.btn_rxjava2:
                observable = createObservable2();
                observable.subscribe(mObserver);
                break;
            case R.id.btn_rxjava3:
                observable = createObservable3();
                observable.subscribe(mObserver);
                break;
            case R.id.btn_rxjava4:
                observable = createObservable4();
                observable.subscribe(mObserver);
                break;
            case R.id.btn_rxjava5:
                Observable<Integer> observableInteger = createObservable5();
                observableInteger.subscribe(mObserverInteger);
                break;
            case R.id.btn_rxjava6:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_rxjava1:
                subscribe(R.id.btn_rxjava1);
                break;
            case R.id.btn_rxjava2:
                subscribe(R.id.btn_rxjava2);
                break;
            case R.id.btn_rxjava3:
                subscribe(R.id.btn_rxjava3);
                break;
            case R.id.btn_rxjava4:
                subscribe(R.id.btn_rxjava4);
                break;
            case R.id.btn_rxjava5:
                subscribe(R.id.btn_rxjava5);
                break;
            case R.id.btn_rxjava6:
                subscribe(R.id.btn_rxjava6);
                break;
        }
    }

    /** 创建Observer观察者,接收字符串 **/
    Observer<String> mObserver = new Observer<String>() {
        /**
         * Disposable：相当于水管（被观察者属于下游，观察者属于下游）, 我们可以把它理解成两根管道之间的一个机关,
         * 当调用它的dispose()方法时, 它就会将两根管道切断, 从而导致下游收不到事件。
         *
         */
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(Constant.TAG,"Observer onSubscribe");
        }

        @Override
        public void onNext(@NonNull String s) {
            Log.d(Constant.TAG,"Observer onNext:"+s);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(Constant.TAG,"Observer onError");
        }

        @Override
        public void onComplete() {
            Log.d(Constant.TAG,"Observer onComplete");
        }
    };

    /** 创建Observer观察者,接收整型 **/
    Observer<Integer> mObserverInteger = new Observer<Integer>(){
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(Constant.TAG,"Observer onSubscribe Integer");
        }

        @Override
        public void onNext(@NonNull Integer integer) {
            Log.d(Constant.TAG,"Observer onNext Integer :"+integer);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(Constant.TAG,"Observer onError Integer");
        }

        @Override
        public void onComplete() {
            Log.d(Constant.TAG,"Observer onComplete Integer");
        }
    };

    /*****************************************创建被观察者 *****************************************/
    /**
     * 创建Observable(被观察者)方式一。
     * Observable即被观察者，它决定什么时候触发事件以及触发怎样的事件。
     * RxJava使用create()方法来创建一个Observable，并为它定义事件触发规则。
     * create传入了一个ObservableOnSubscribe对象作为参数，ObservableOnSubscribe会被存储在返回的Observable对象中，
     * 它的作用相当于一个计划表，当Observable被订阅的时候，ObservableOnSubscribe的subscribe()方法会自动被调用，
     * 事件序列就会依照设定依次触发(对于上面的代码，就是观察者ObservableEmitter将会被调用三次onNext()和一次onCompleted())。
     * 这样，由被观察者调用了观察者的回调方法，就实现了由被观察者向观察者的事件传递，即观察者模式。
     * create()方法是RxJava最基本的创造事件序列的方法。
     *
     */
    private Observable<String> createObservable1(){
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
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
                Log.d(Constant.TAG,"Observable subscribe");
                e.onNext("create 1");
                e.onNext("create 2");
                e.onNext("create 3");
                e.onComplete();
            }
        });
        return observable;
    }


    /**
     * 创建Observable(被观察者)方式二。
     * just(T...):将传入的参数依次发送出来
     * 将会依次调用：
     * onNext("Hello");
     * onNext("Hi");
     * onNext("Aloha");
     * onCompleted();
     * 都和之前的create()是等价的
     */
    private Observable<String> createObservable2(){
        Observable<String> observable = Observable.just("just 1", "just 2", "just 3");
        return observable;
    }


    /**
     * 创建Observable(被观察者)方式三。
     * 使用fromIterable()，遍历集合，发送每个item，相当于多次回调onNext()方法，每次传入一个item。
     */
    private Observable<String> createObservable3(){
        Observable<String> observable = Observable.fromIterable(createList());
        return observable;
    }

    /**
     * 创建Observable(被观察者)方式四。
     * 当观察者订阅时，才创建Observable，并且针对每个观察者创建都是一个新的Observable。
     * 以何种方式创建这个Observable对象，当满足回调条件后，就会进行相应的回调。
     */
    private Observable<String> createObservable4(){
        Observable<String> observable = Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just("defer 1","defer 2");
            }
        });
        return observable;
    }

    /**
     * 创建Observable(被观察者)方式五。
     * 创建一个按固定时间间隔发射整数序列的Observable，可用作定时器。即按照固定2秒一次调用onNext()方法。
     * 创建一个发射特定整数序列的Observable，第一个参数为起始值，第二个为发送的个数，如果为0则不发送，负数则抛异常。
     * 如下表示发射1到20的数，即调用20次nNext()方法，依次传入1-20数字。
     */
    private Observable<Integer> createObservable5(){
        Observable<Integer> observable = Observable.range(1,20);
        return observable;
    }

    /**
     * 创建Observable(被观察者)方式六。
     * 创建一个Observable，它在一个给定的延迟后发射一个特殊的值，即表示延迟2秒后，调用onNext()方法。
     */
    private Observable<Long> createObservable6(){
        Observable<Long> observable = Observable.timer(1, TimeUnit.SECONDS);
        return observable;
    }

    /**
     * 创建Observable(被观察者)方式七。
     */
    private Observable<String> createObservable7(){
        String[] words = {"have", "a", "fun"};
        /**
         * 将传入的数组或 Iterable 拆分成具体对象后，依次发送出来。
         * 将会依次调用：
         * onNext("have");
         * onNext("a");
         * onNext("fun");
         * onCompleted();
         * 都和之前的create()是等价的
         */
        Observable<String> observable = Observable.fromArray(words);
        return observable;
    }

    /*********观察者模式的实现：Observable(被观察者)只有在被Observer(观察者)订阅后才能执行其内部的相关逻辑 ***********/
    /** 方式一 **/
    private void subscribe1(){
        Observable.just("hello").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(Constant.TAG,"Consumer : "+s);
            }
        });
    }

    /****************************************************** 操作符 *********************************************/
    // 操作符就是用于在Observable和最终的Observer之间，通过转换Observable为其他观察者对象的过程，
    // 修改发出的事件，最终将最简洁的数据传递给Observer对象。

    /**
     * map()操作符，就是把原来的Observable对象转换成另一个Observable对象，同时将传输的数据进行一些灵活的操作，
     * 方便Observer获得想要的数据形式。
     */
    private void mapWay(){
        Observable<Integer> observable = Observable.just("hello").map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return s.length();
            }
        });
    }

    /**
     * flatMap()对于数据的转换比map()更加彻底，如果发送的数据是集合，flatmap()重新生成一个Observable对象，
     * 并把数据转换成Observer想要的数据形式。它可以返回任何它想返回的Observable对象。
     */
    private void flatMapWay(){
        Observable<Object> observable = Observable.just(createList()).flatMap(new Function<List<String>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(List<String> strings) throws Exception {
                return Observable.fromIterable(strings);
            }
        });
    }

    /**
     * filter()操作符根据test()方法中，根据自己想过滤的数据加入相应的逻辑判断，返回true则表示数据满足条件，
     * 返回false则表示数据需要被过滤。最后过滤出的数据将加入到新的Observable对象中，方便传递给Observer想要的数据形式。
     */
    private void filterWay(){
        Observable.just(createList()).flatMap(new Function<List<String>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(List<String> strings) throws Exception {
                return Observable.fromIterable(strings);
            }
        }).filter(new Predicate<Object>() {
            @Override
            public boolean test(Object s) throws Exception {
                String newStr = (String) s;
                if (newStr.charAt(5) - '0' > 5) {
                    return true;
                }
                return false;
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(Constant.TAG,"filter : "+(String)o);
            }
        });
    }

    /**
     * take()操作符输出最多指定数量的结果。
     */
    private void takeWay(){
        Observable.just(createList()).flatMap(new Function<List<String>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(List<String> strings) throws Exception {
                return Observable.fromIterable(strings);
            }
        }).take(5).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object s) throws Exception {
                Log.d(Constant.TAG,"take : "+(String)s);
            }
        });
    }

    /**
     * doOnNext()允许我们在每次输出一个元素之前做一些额外的事情
     */
    private void doOnNextWay(){
        Observable.just(createList()).flatMap(new Function<List<String>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(List<String> strings) throws Exception {
                return Observable.fromIterable(strings);
            }
        }).take(5).doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(Constant.TAG,"doOnNext : 准备工作");
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object s) throws Exception {
                System.out.println((String)s);
            }
        });
    }

    private List<String> createList(){
        List<String> list = new ArrayList<String>();
        for(int i =0;i<10;i++){
            list.add("list"+i);
        }
        return list;
    }
}
