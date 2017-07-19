package com.huier.fw_rxjava.interfaces;

import com.huier.fw_rxjava.entity.LoginRequest;
import com.huier.fw_rxjava.entity.LoginResponse;
import com.huier.fw_rxjava.entity.RegisterRequest;
import com.huier.fw_rxjava.entity.RegisterResponse;
import com.huier.fw_rxjava.entity.UserBaseInfoRequest;
import com.huier.fw_rxjava.entity.UserBaseInfoResponse;
import com.huier.fw_rxjava.entity.UserExtraInfoRequest;
import com.huier.fw_rxjava.entity.UserExtraInfoResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 作者：张玉辉
 * 时间：2017/7/19.
 */

public interface Api {
    @GET
    Observable<LoginResponse> login(@Body LoginRequest request);

    @GET
    Observable<RegisterResponse> register(@Body RegisterRequest request);

    @GET
    Observable<UserBaseInfoResponse> getUserBaseInfo(@Body UserBaseInfoRequest request);

    @GET
    Observable<UserExtraInfoResponse> getUserExtraInfo(@Body UserExtraInfoRequest request);

    @GET("v2/movie/top250")
    Observable<Response<ResponseBody>> getTop250(@Query("start") int start, @Query("count") int count);
}
