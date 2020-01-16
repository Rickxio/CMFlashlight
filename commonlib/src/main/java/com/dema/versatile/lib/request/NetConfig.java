package com.dema.versatile.lib.request;

import okhttp3.Interceptor;

public interface NetConfig {

    /**
     * @return 配置baseUrl
     */
    String configBaseUrl();

    /**
     * @return 拦截器
     */
    Interceptor[] configInterceptors();

    /**
     * @return 连接超时时间（毫秒）
     */
    long configConnectTimeoutMills();
    /**
     * @return 读取超时时间（毫秒）
     */
    long configReadTimeoutMills();
    /**
     * @return 是否打开调试模式
     */
    boolean configLogEnable();

}
