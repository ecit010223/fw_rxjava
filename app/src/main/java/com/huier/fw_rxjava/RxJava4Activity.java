package com.huier.fw_rxjava;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 线程调度器的使用，参考：http://www.jianshu.com/p/310726a75045
 * 在不指定线程的情况下，RxJava遵循的是线程不变的原则，即：在哪个线程调用subscribe()，就在哪个线程生产事件；
 * 在哪个线程生产事件，就在哪个线程消费事件，如果需要切换线程，就需要用到Scheduler（调度器）。
 * Scheduler，相当于线程控制器，RxJava通过它来指定每一段代码应该运行在什么样的线程。
 * RxJava已经内置了几个Scheduler，它们已经适合大多数的使用场景。
 */
public class RxJava4Activity extends AppCompatActivity {


    public static void entry(Context from){
        Intent intent = new Intent(from,RxJava4Activity.class);
        from.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java4);
    }
}
