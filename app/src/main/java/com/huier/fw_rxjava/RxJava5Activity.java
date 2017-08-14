package com.huier.fw_rxjava;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 参考：http://www.jianshu.com/p/1f4867ce3c01
 * 在原来的RxJava 1.x版本中并没有Flowable的存在，Backpressure问题是由Observable来处理的。
 * 在RxJava 2.x中对于backpressure的处理进行了改动，为此将原来的Observable拆分成了新的Observable和Flowable，
 * 同时其他相关部分也同时进行了拆分，原先的Observable已经不具备背压处理能力。
 * Flowable是为了应对Backpressure而产生的，Flowable是一个被观察者，与Subscriber(观察者)配合使用，解决Backpressure问题。
 * 处理Backpressure的策略仅仅是处理Subscriber接收事件的方式，并不影响Flowable发送事件的方法。
 * 即使采用了处理Backpressure的策略，Flowable原来以什么样的速度产生事件，现在还是什么样的速度不会变化，
 * 主要处理的是Subscriber接收事件的方式。
 */
public class RxJava5Activity extends AppCompatActivity implements View.OnClickListener {
    private Button btnErrorExample;
    private Button btnMissingBackpressure;
    private Button btnBackpressureError;
    private Button btnBackpressureDrop;
    /** 可对事件传递管道进行管理 **/
    private Subscription mSubscription;
    /** 被观察者 **/
    private Flowable mFlowable;
    /** 观察者 **/
    private Subscriber mSubscriber;

    public static void entry(Context from) {
        Intent intent = new Intent(from, RxJava5Activity.class);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java5);
        btnErrorExample = (Button) findViewById(R.id.btn_error_example);
        btnErrorExample.setOnClickListener(this);
        btnMissingBackpressure = (Button) findViewById(R.id.btn_missing_backpressure);
        btnMissingBackpressure.setOnClickListener(this);
        btnBackpressureError = (Button)findViewById(R.id.btn_backpressure_error);
        btnBackpressureError.setOnClickListener(this);
        btnBackpressureDrop = (Button)findViewById(R.id.btn_backpressure_drop);
        btnBackpressureDrop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_error_example:
                errorExample();
                break;
            case R.id.btn_missing_backpressure:
                missingBackpressure();
                break;
            case R.id.btn_backpressure_error:
                backpressureStrategyERROR();
                break;
            case R.id.btn_backpressure_drop:
                backpressureStrategyDROP();
                break;
        }
    }

    /**
     * 当被观察者发送消息十分迅速以至于观察者不能及时的响应这些消息(Backpressure,即背压问题)。
     * 被观察者是事件的生产者，观察者是事件的消费者。上述例子中可以看出生产者无限生成事件，
     * 而消费者每2秒才能消费一个事件，这会造成事件无限堆积，最后造成OOM。
     */
    private void errorExample() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                while (true) {
                    e.onNext(1);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Thread.sleep(2000);
                        Log.d(Constant.TAG, integer.toString());
                    }
                });
    }

    private void missingBackpressure() {
        //被观察者
        Flowable<Integer> flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                Log.d(Constant.TAG, "emit 1");
                emitter.onNext(1);
                Log.d(Constant.TAG, "emit 2");
                emitter.onNext(2);
                Log.d(Constant.TAG, "emit 3");
                emitter.onNext(3);
                Log.d(Constant.TAG, "emit complete");
                emitter.onComplete();
            }
        }, BackpressureStrategy.ERROR); //增加了一个参数
        //观察者
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            /**
             * 传给接收者的不是Disposable，而是Subscription。
             * Subscription也可以用于切断观察者与被观察者之间的联系，调用Subscription.cancel()方法便可。
             * @param s
             */
            @Override
            public void onSubscribe(Subscription s) {
                Log.d(Constant.TAG, "onSubscribe");
                /**
                 * 用来向生产者申请可以消费的事件数量,这样我们便可以根据本身的消费能力进行消费事件。
                 * 当调用了request()方法后，生产者便发送对应数量的事件供消费者消费。
                 * 注意：如果不显示调用request就表示消费能力为0。
                 * 虽然并不限制向request()方法中传入任意数字，但是如果消费者并没有这么多的消费能力，依旧会造成资源浪费，
                 * 最后产生OOM。形象点就是不能打肿脸充胖子，而ERROR策略就避免了这种情况的出现。
                 * 在异步调用时，RxJava中有个缓存池，用来缓存消费者处理不了暂时缓存下来的数据，缓存池的默认大小为128，
                 * 即只能缓存128个事件，无论request()中传入的数字比128大或小，缓存池中在刚开始都会存入128个事件，
                 * 当然如果本身并没有这么多事件需要发送，则不会存128个事件。
                 * 在ERROR策略下，如果缓存池溢出，就会立刻抛出MissingBackpressureException异常。
                 */
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(Constant.TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.d(Constant.TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(Constant.TAG, "onComplete");
            }
        };
        flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private void backpressureStrategyERROR(){
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 129; i++) {
                    Log.d(Constant.TAG, "emit " + i);
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(Constant.TAG, "onNext: " + integer);
                    }
                    @Override
                    public void onError(Throwable t) {
                        Log.d(Constant.TAG, "onError: ", t);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * BackpressureStrategy.DROP
     * 消费者通过request()传入其需求n，然后生产者把n个事件传递给消费者供其消费,其他消费不掉的事件就丢掉。
     * BackpressureStrategy.BUFFER
     * BUFFER把RxJava中默认的只能存128个事件的缓存池换成一个大的缓存池，支持存很多很多的数据，这样，
     * 消费者通过request()即使传入一个很大的数字，生产者也会生产事件，并将处理不了的事件缓存。
     * 但是这种方式任然比较消耗内存，除非是我们比较了解消费者的消费能力，能够把握具体情况，不会产生OOM。
     * 总之BUFFER要慎用。
     * BackpressureStrategy.LATEST
     * LATEST与DROP功能基本一致，消费者通过request()传入其需求n，然后生产者把n个事件传递给消费者供其消费。
     * 其他消费不掉的事件就丢掉，唯一的区别就是LATEST总能使消费者能够接收到生产者产生的最后一个事件。
     */
    private void backpressureStrategyDROP(){
        mFlowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; ; i++) {
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.DROP);
        mSubscriber = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                mSubscription = s;
                s.request(50);
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(Constant.TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.d(Constant.TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                Log.d(Constant.TAG, "onComplete");
            }
        };
        start();
    }

    /**
     * 如果Flowable对象不是自己创建的，可以采用onBackpressureBuffer()、onBackpressureDrop()、onBackpressureLatest()的方式指定。
     */
    private void createFlowableByJust(){
        Flowable.just(1).onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });
    }

    /** 建立连接，生产者开始生产事件 **/
    public void start(){
        mFlowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mSubscriber);
    }

    /** 刚开始消费者通过request()只要了50个事件消费，然后每次点击“消费”按钮，再次消费50个事件 **/
    public void consume(View view){
        mSubscription.request(50);
    }
}
