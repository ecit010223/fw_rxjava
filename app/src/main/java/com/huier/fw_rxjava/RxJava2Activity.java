package com.huier.fw_rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 上游：被观察者
 * 下游：观察者
 * 正常情况下,上游和下游是工作在同一个线程中的,也就是说上游在哪个线程发事件,下游就在哪个线程接收事件。
 */
public class RxJava2Activity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "tag";
    private Button btnRxJava2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java2);
        Log.d(TAG, Thread.currentThread().getName());
        initView();
    }

    private void initView(){
        btnRxJava2 = (Button)findViewById(R.id.btn_rxjava2);
        btnRxJava2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_rxjava2:
                subscribe1();
                break;
        }
    }

    /**
     * 子线程中做耗时的操作, 然后回到主线程中来操作UI。
     * 改变上游发送事件的线程,让它去子线程中发送事件,然后再改变下游的线程,让它去主线程接收事件，
     * 通过RxJava内置的线程调度器可以很轻松的做到这一点。
     */
    private void subscribe1(){
        observable.subscribeOn(Schedulers.newThread())  // subscribeOn指定的是上游发送事件的线程，
                .observeOn(AndroidSchedulers.mainThread())  // observeOn()指定的是下游接收事件的线程，
                .subscribe(consumer);
    }

    /**
     * 多次指定上游的线程只有第一次指定的有效,也就是说多次调用subscribeOn()只有第一次的有效,其余的会被忽略。
     * 多次指定下游的线程是可以的,也就是说每调用一次observeOn(),下游的线程就会切换一次。
     * 在RxJava中,已经内置了很多线程选项供我们选择, 如有：
     * (1)Schedulers.io()代表io操作的线程, 通常用于网络,读写文件等io密集型的操作;
     * (2)Schedulers.computation()代表CPU计算密集型的操作, 例如需要大量计算的操作;
     * (3)Schedulers.newThread()代表一个常规的新线程;
     * (4)AndroidSchedulers.mainThread()代表Android的主线程。
     * 这些内置的Scheduler已经足够满足我们开发的需求,因此我们应该使用内置的这些选项,在RxJava内部使用的是线程池
     * 来维护这些线程,所有效率也比较高。
     */
    private void subscribe2(){
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())  //指定了两次上游发送事件的线程, 分别是newThread和IO线程
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())  //下游也指定了两次线程,分别是main和IO线程
                .subscribe(consumer);
    }

    /** 每调用一次observeOn() 线程便会切换一次 **/
    private void subscribe3(){
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(mainThread), current thread is: " + Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "After observeOn(io), current thread is : " + Thread.currentThread().getName());
                    }
                })
                .subscribe(consumer);
    }

    Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
        @Override
        public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
            Log.d(TAG, "Observable thread is : " + Thread.currentThread().getName());
            Log.d(TAG, "emit 1");
            emitter.onNext(1);
        }
    });

    Consumer<Integer> consumer = new Consumer<Integer>() {
        @Override
        public void accept(Integer integer) throws Exception {
            Log.d(TAG, "Observer thread is :" + Thread.currentThread().getName());
            Log.d(TAG, "onNext: " + integer);
        }
    };
}
