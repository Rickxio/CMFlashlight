package com.dema.versatile.lib.intf;

import android.database.Observable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author w7
 */
public interface ApiService {

    /**
     *  获取数据
     * @param url 数据url
     * @param body body数据
     * @return 返回数据
     */
    @POST
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Observable<ResponseBody> postData(@Url String url,@Body RequestBody body);

}