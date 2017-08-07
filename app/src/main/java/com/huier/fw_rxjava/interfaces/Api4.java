package com.huier.fw_rxjava.interfaces;

import com.huier.fw_rxjava.entity.rxjava4.AllCity;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * 作者：张玉辉
 * 时间：2017/8/7.
 * 应用于RxJava4Activity
 */

public interface Api4 {
    @GET("citys")
    Observable<AllCity> getAllCity();
}
