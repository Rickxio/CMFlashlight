package com.dema.versatile.lib.core.in;

import java.util.List;
import java.util.Map;

public interface ICMHttpResult extends ICMObj {
    boolean isSuccess();

    int getResponseCode();

    byte[] getBuffer();

    Map<String, List<String>> getHeaderFieldMap();

    String getException();

    /**
     * 获取下载文件的总大小
     * 单位byte
     *
     * @return
     */
    int getDownloadTotalSize();

    /**
     * 获取下载文件的当前下载的大小
     * 单位byte
     *
     * @return
     */
    int getDownloadCurrentSize();

    /**
     * 获取下载进度
     *
     * @return
     */
    double getDownloadProgress();
}
