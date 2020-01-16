package com.dema.versatile.lib.core.in;

import java.util.Map;

public interface ICMHttp extends ICMObj, ICMObserver<ICMHttpListener> {

    ICMHttpResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty);

    ICMHttpResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, boolean bIsNeedEncrypt);

    ICMHttpResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);

    void requestToBufferByPostAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, ICMHttpListener iCMHttpListener);

    void requestToBufferByPostAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, ICMHttpListener iCMHttpListener, boolean bIsNeedEncrypt);

    void requestToBufferByPostAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, ICMHttpListener iCMHttpListener, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt);
}
