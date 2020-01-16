package com.dema.versatile.lib.core.im;

import android.text.TextUtils;
import android.util.Log;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMHttp;
import com.dema.versatile.lib.core.in.ICMHttpListener;
import com.dema.versatile.lib.core.in.ICMHttpResult;
import com.dema.versatile.lib.core.in.ICMThreadPool;
import com.dema.versatile.lib.core.in.ICMThreadPoolListener;
import com.dema.versatile.lib.utils.UtilsEncrypt;
import com.dema.versatile.lib.utils.UtilsEnv;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class CMHttp extends CMObserverIntelligence<ICMHttpListener> implements ICMHttp {
    private ICMThreadPool mICMThreadPool = null;

    private static final int VALUE_INT_CONNECT_TIMEOUT = 12 * 1000;
    private static final int VALUE_INT_READ_TIMEOUT = 12 * 1000;

    public CMHttp() {
        _init();
    }

    private void _init() {
        mICMThreadPool = CMLibFactory.getInstance().createInstance(ICMThreadPool.class);
    }

    @Override
    public ICMHttpResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty) {
        return requestToBufferByPostSync(strURL, mapParam, mapRequestProperty, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT, true);
    }

    @Override
    public ICMHttpResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, boolean bIsNeedEncrypt) {
        return requestToBufferByPostSync(strURL, mapParam, mapRequestProperty, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT, bIsNeedEncrypt);
    }

    @Override
    public ICMHttpResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL))
            return null;

        HttpToolTask httpToolTask = new HttpToolTask();
        httpToolTask.mRequestType = HttpToolTask.VALUE_INT_REQUEST_POST_TYPE;
        httpToolTask.mConnectTimeout = nConnectTimeout;
        httpToolTask.mReadTimeout = nReadTimeout;
        httpToolTask.mURL = strURL;
        httpToolTask.mMapParam = mapParam;
        httpToolTask.mMapRequestProperty = mapRequestProperty;
        httpToolTask.mIsNeedEncrypt = bIsNeedEncrypt;

        CMHttpResult cmHttpResult = (CMHttpResult) CMLibFactory.getInstance().createInstance(ICMHttpResult.class, CMHttpResult.class);
        requestByPost(httpToolTask, cmHttpResult);
        return cmHttpResult;
    }

    @Override
    public void requestToBufferByPostAsync(final String strURL, final Map<String, String> mapParam, final Map<String, String> mapRequestProperty, final Object objectTag, final ICMHttpListener iCMHttpListener) {
        requestToBufferByPostAsync(strURL, mapParam, mapRequestProperty, objectTag, iCMHttpListener, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT, true);
    }

    @Override
    public void requestToBufferByPostAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, ICMHttpListener iCMHttpListener, boolean bIsNeedEncrypt) {
        requestToBufferByPostAsync(strURL, mapParam, mapRequestProperty, objectTag, iCMHttpListener, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT, bIsNeedEncrypt);
    }

    @Override
    public void requestToBufferByPostAsync(final String strURL, final Map<String, String> mapParam, final Map<String, String> mapRequestProperty, final Object objectTag, final ICMHttpListener iCMHttpListener, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL))
            return;

        final HttpToolTask httpToolTask = new HttpToolTask();
        httpToolTask.mRequestType = HttpToolTask.VALUE_INT_REQUEST_POST_TYPE;
        httpToolTask.mConnectTimeout = nConnectTimeout;
        httpToolTask.mReadTimeout = nReadTimeout;
        httpToolTask.mURL = strURL;
        httpToolTask.mMapParam = mapParam;
        httpToolTask.mMapRequestProperty = mapRequestProperty;
        httpToolTask.mIsNeedEncrypt = bIsNeedEncrypt;

        final CMHttpResult cmHttpResult = (CMHttpResult) CMLibFactory.getInstance().createInstance(ICMHttpResult.class, CMHttpResult.class);
        mICMThreadPool.run(new ICMThreadPoolListener() {
            @Override
            public void onRun() {
                requestByPost(httpToolTask, cmHttpResult);
            }

            @Override
            public void onComplete() {
                if (null != iCMHttpListener) {
                    iCMHttpListener.onRequestToBufferByPostAsyncComplete(strURL, mapParam, objectTag, cmHttpResult);
                } else {
                    for (ICMHttpListener listener : getListenerList())
                        listener.onRequestToBufferByPostAsyncComplete(strURL, mapParam, objectTag, cmHttpResult);
                }
            }
        });
    }

    private void requestByPost(HttpToolTask httpToolTask, CMHttpResult cmHttpResult) {
        if (null == httpToolTask || null == cmHttpResult)
            return;

        try {
            URL url = new URL(httpToolTask.mURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(httpToolTask.mConnectTimeout);
            httpURLConnection.setReadTimeout(httpToolTask.mReadTimeout);

            if (null != httpToolTask.mMapRequestProperty && !httpToolTask.mMapRequestProperty.isEmpty()) {
                for (Map.Entry<String, String> entry : httpToolTask.mMapRequestProperty.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            httpURLConnection.connect();

            if (null != httpToolTask.mMapParam && !httpToolTask.mMapParam.isEmpty()) {
                StringBuffer sbParam = new StringBuffer();
                for (Map.Entry<String, String> entry : httpToolTask.mMapParam.entrySet()) {
                    sbParam.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }

                sbParam.deleteCharAt(sbParam.length() - 1);

                OutputStream os = httpURLConnection.getOutputStream();
                if (httpToolTask.mIsNeedEncrypt) {
                    byte[] bytes = UtilsEncrypt.encryptByBlowFish(sbParam.toString().getBytes());
                    os.write(bytes);
                } else {
                    os.write(sbParam.toString().getBytes());
                }

                os.flush();
                os.close();
            }

            int nResponseCode = httpURLConnection.getResponseCode();
            cmHttpResult.setResponseCode(nResponseCode);

            InputStream is = null;
            if (nResponseCode == HttpURLConnection.HTTP_OK
                    || nResponseCode == HttpURLConnection.HTTP_CREATED
                    || nResponseCode == HttpURLConnection.HTTP_ACCEPTED) {
                is = httpURLConnection.getInputStream();
            } else {
                is = httpURLConnection.getErrorStream();
            }

            int nAllSize = 0;
            int nReadSize = 0;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];

            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bis.available());
            while ((nReadSize = bis.read(buffer)) > 0) {
                nAllSize += nReadSize;
                baos.write(buffer, 0, nReadSize);
            }

            is.close();
            bis.close();
            baos.close();

            if (nResponseCode == HttpURLConnection.HTTP_OK
                    || nResponseCode == HttpURLConnection.HTTP_CREATED
                    || nResponseCode == HttpURLConnection.HTTP_ACCEPTED) {
                cmHttpResult.setSuccess(true);
                cmHttpResult.setHeaderFieldMap(httpURLConnection.getHeaderFields());
                if (httpToolTask.mIsNeedEncrypt) {
                    cmHttpResult.setBuffer(UtilsEncrypt.decryptByBlowFish(baos.toByteArray()));
                } else {
                    cmHttpResult.setBuffer(baos.toByteArray());
                }
            } else {
                cmHttpResult.setException(baos.toString());
            }
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            cmHttpResult.setException(e.toString());
        }
    }

    class HttpToolTask {
        public int mRequestType = VALUE_INT_REQUEST_GET_TYPE;
        public int mConnectTimeout = VALUE_INT_CONNECT_TIMEOUT;
        public int mReadTimeout = VALUE_INT_READ_TIMEOUT;
        public String mURL = null;
        public Map<String, String> mMapParam = null;
        public Map<String, String> mMapRequestProperty = null;
        public boolean mIsNeedEncrypt = false;

        public static final int VALUE_INT_REQUEST_GET_TYPE = 0x1000;
        public static final int VALUE_INT_REQUEST_POST_TYPE = 0x1001;
    }
}
