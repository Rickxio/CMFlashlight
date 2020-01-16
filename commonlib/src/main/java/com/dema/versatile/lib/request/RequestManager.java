package com.dema.versatile.lib.request;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestManager {

    /**
     * 网络配置项
     */
    private static NetConfig mConfig = null;
    /**
     * mRetrofit
     */
    private Retrofit mRetrofit;
    /**
     * mClient
     */
    private OkHttpClient mClient;
    /**
     * 默认连接超时时间
     */
    private static final long DEFAULT_CONNECT_TIMEOUT_MILLS = 10 * 1000L;
    /**
     * 默认读取超时时间
     */
    private static final long DEFAULT_READ_TIMEOUT_MILLS = 10 * 1000L;
    /**
     * 实例
     **/
    private static RequestManager mInstance;

    private RequestManager(){
        mRetrofit = null;
        mClient = null;
    }

    public synchronized static RequestManager getInstance(){
        if (mInstance == null) {
            mInstance = new RequestManager();
        }
        return mInstance;
    }

    public static void registerConfig(NetConfig config){
        mConfig = config;
        mInstance = null;
    }

    public Retrofit getRetrofit(){
        if(mRetrofit == null){
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(mConfig.configBaseUrl())//配置BaseUrl
                    .client(getOkHttpClient())// 设置client
                    .addConverterFactory(GsonConverterFactory.create());//Gson转换器
            builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            mRetrofit = builder.build();
        }
        return mRetrofit;
    }

    public OkHttpClient getOkHttpClient() {
        if (mClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            // 连接超时时间
            builder.connectTimeout(mConfig.configConnectTimeoutMills() != 0
                    ? mConfig.configConnectTimeoutMills()
                    : DEFAULT_CONNECT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);
            // 读取超时时间
            builder.readTimeout(mConfig.configReadTimeoutMills() != 0
                    ? mConfig.configReadTimeoutMills() : DEFAULT_READ_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);

            // 拦截器
            Interceptor[] interceptors = mConfig.configInterceptors();
            if (interceptors != null && interceptors.length > 0) {
                for (Interceptor interceptor : interceptors) {
                    builder.addInterceptor(interceptor);
                }
            }
            if (mConfig.configLogEnable()) {//配置打印
                HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
                logInterceptor.level(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(logInterceptor);
            }

            mClient = builder.build();
        }
        return mClient;
    }

    public <C> C get(Class<C> service){
        return getInstance().getRetrofit().create(service);
    }

}
