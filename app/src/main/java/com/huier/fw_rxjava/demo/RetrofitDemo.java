package com.huier.fw_rxjava.demo;

import android.content.Context;
import android.util.Log;

import com.huier.fw_rxjava.interfaces.Api;
import com.huier.fw_rxjava.utils.RetrofitProvider;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 作者：张玉辉
 * 时间：2017/7/19.
 */

public class RetrofitDemo {
    private static final String TAG = "tag";
    public static void demo1(final Context context) {
        final Api api = RetrofitProvider.get().create(Api.class);
        api.getTop250(0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> response) throws Exception {
                        Log.d(TAG, response.body().string());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.w("Error", throwable);
                    }
                });
    }
}
