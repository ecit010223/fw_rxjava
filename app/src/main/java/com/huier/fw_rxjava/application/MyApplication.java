package com.huier.fw_rxjava.application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * 作者：张玉辉
 * 时间：2017/8/7.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(LeakCanary.isInAnalyzerProcess(this)){
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
