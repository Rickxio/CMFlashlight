package com.dema.versatile.lib.core.im;

import java.util.List;
import java.util.Map;

import com.dema.versatile.lib.core.in.ICMHttpResult;

public class CMHttpResult implements ICMHttpResult {
    private boolean mSuccess = false;
    private int mResponseCode = 0;
    private byte[] mBuffer = null;
    private Map<String, List<String>> mMapHeaderField = null;
    private String mException = null;
    private int mDownloadTotalSize = 0;
    private int mDownloadCurrentSize = 0;

    public CMHttpResult() {
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean isSuccess() {
        return mSuccess;
    }

    @Override
    public int getResponseCode() {
        return mResponseCode;
    }

    @Override
    public byte[] getBuffer() {
        return mBuffer;
    }

    @Override
    public Map<String, List<String>> getHeaderFieldMap() {
        return mMapHeaderField;
    }

    @Override
    public String getException() {
        return mException;
    }

    public void setSuccess(boolean bSuccess) {
        mSuccess = bSuccess;
    }

    public void setResponseCode(int nResponseCode) {
        mResponseCode = nResponseCode;
    }

    public void setBuffer(byte[] buffer) {
        mBuffer = buffer;
    }

    public void setHeaderFieldMap(Map<String, List<String>> mapHeaderField) {
        mMapHeaderField = mapHeaderField;
    }

    public void setException(String strException) {
        mException = strException;
    }

    @Override
    public int getDownloadTotalSize() {
        return mDownloadTotalSize;
    }

    @Override
    public int getDownloadCurrentSize() {
        return mDownloadCurrentSize;
    }

    @Override
    public double getDownloadProgress() {
        if (mDownloadTotalSize == 0) {
            return 0;
        }
        return mDownloadCurrentSize * 1.0d / mDownloadTotalSize;
    }

    public void setDownloadTotalSize(int downloadTotalSize) {
        mDownloadTotalSize = downloadTotalSize;
    }

    public void setDownloadCurrentSize(int downloadCurrentSize) {
        mDownloadCurrentSize = downloadCurrentSize;
    }
}
