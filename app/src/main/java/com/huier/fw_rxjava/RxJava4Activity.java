package com.huier.fw_rxjava;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huier.fw_rxjava.entity.rxjava4.AllCity;
import com.huier.fw_rxjava.entity.rxjava4.City;
import com.huier.fw_rxjava.interfaces.Api4;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 线程调度器的使用，参考：http://www.jianshu.com/p/310726a75045
 * 在不指定线程的情况下，RxJava遵循的是线程不变的原则，即：在哪个线程调用subscribe()，就在哪个线程生产事件；
 * 在哪个线程生产事件，就在哪个线程消费事件，如果需要切换线程，就需要用到Scheduler（调度器）。
 * Scheduler，相当于线程控制器，RxJava通过它来指定每一段代码应该运行在什么样的线程。
 * RxJava已经内置了几个Scheduler，它们已经适合大多数的使用场景。
 * Schedulers.immediate()：直接在当前线程运行，相当于不指定线程，这是默认的Scheduler。
 * Schedulers.newThread()：总是启用新线程，并在新线程执行操作。
 * Schedulers.io()：I/O操作(读写文件、读写数据库、网络信息交互等)所使用的Scheduler,行为模式和newThread()差不多，
 *     区别在于io()的内部实现是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下io()比newThread()更有效率。
 *     不要把计算工作放在io()中，可以避免创建不必要的线程。
 * Schedulers.computation()：复杂计算所使用的Scheduler，这个计算指的是CPU密集型计算，即不会被I/O等操作限制性能的操作，
 *     例如图形的计算。这个Scheduler使用固定的线程池，大小为CPU核数。不要把I/O操作放在computation()中，
 *     否则I/O操作的等待时间会浪费CPU。
 * AndroidSchedulers.mainThread()：它指定的操作将在Android主线程运行。
 * subscribeOn()：指定Observable(被观察者)所在的线程，或者叫做事件产生的线程。
 * observeOn()：指定Observer(观察者)所运行在的线程，或者叫做事件消费的线程。
 */
public class RxJava4Activity extends AppCompatActivity implements View.OnClickListener {
    private static final String URL = "http://192.168.1.211:8081/rxjava/";
    private Button btnScheduler;
    private Button btnIOScheduler;

    public static void entry(Context from){
        Intent intent = new Intent(from,RxJava4Activity.class);
        from.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java4);
        btnScheduler = (Button)findViewById(R.id.btn_scheduler);
        btnScheduler.setOnClickListener(this);
        btnIOScheduler = (Button)findViewById(R.id.btn_io_schoduler);
        btnIOScheduler.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scheduler:
                basicScheduler();
                break;
            case R.id.btn_io_schoduler:
                ioScheduler();
                break;
        }
    }

    private void basicScheduler(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(Constant.TAG,"发送数据所在的线程："+Thread.currentThread().getName());
                Log.d(Constant.TAG, "发送的数据："+1);
                e.onNext(1);
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Consumer<Integer>() {
              @Override
              public void accept(Integer integer) throws Exception {
                  Log.d(Constant.TAG,"接收数据所在的线程："+Thread.currentThread().getName());
                  Log.d(Constant.TAG, "接收到的数据："+"integer：" + integer);
            }
        });
    }

    /**
     * 先使用flatMap()操作符先将封装所有信息的AllCity中提取出城市信息集合，然后转换成一个新的Observable(被观察者)
     * 进行传递，然后使用filter()进行过滤，过滤出符合要求的城市信息，最终传递给Observer(观察者)，
     * 让其在UI线程接收数据，然后更新UI。
     */
    private void ioScheduler(){
        Retrofit retrofit = create();
        Api4 api = retrofit.create(Api4.class);
        Observable<AllCity> observable = api.getAllCity();
        observable.subscribeOn(Schedulers.io())
                .flatMap(new Function<AllCity, ObservableSource<City>>() {
                    @Override
                    public ObservableSource<City> apply(AllCity city) throws Exception {
                        ArrayList<City> result = city.getResult();
                        return Observable.fromIterable(result);
                    }
                })
                .filter(new Predicate<City>() {
                    @Override
                    public boolean test(City city) throws Exception {
                        String id = city.getId();
                        if(Integer.parseInt(id)<5){
                            return true;
                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<City>() {
                    @Override
                    public void accept(City city) throws Exception {
                        Log.d(Constant.TAG,city.toString());
                    }
                });
    }

     /** 创建一个Retrofit客户端 **/
     private Retrofit create() {
         OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
         builder.readTimeout(0, TimeUnit.SECONDS);
         builder.connectTimeout(0, TimeUnit.SECONDS);

         return new Retrofit.Builder().baseUrl(URL)
                 .client(builder.build())
                 .addConverterFactory(GsonConverterFactory.create())
                 .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                 .build();
     }
}
