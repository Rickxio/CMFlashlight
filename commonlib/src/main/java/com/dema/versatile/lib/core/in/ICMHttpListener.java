package com.dema.versatile.lib.core.in;

import java.util.List;
import java.util.Map;

public abstract class ICMHttpListener {
    public void onRequestToBufferByGetAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, ICMHttpResult iCMHttpResult){}

    public void onRequestToBufferByPostAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, ICMHttpResult iCMHttpResult){}

    public void onUploadFileByPostAsyncComplete(String strURL, Map<String, String> mapParam, List<ICMHttpFile> listUploadFile, Object objectTag, ICMHttpResult iCMHttpResult){}

    public void onDownLoading(String strURL, Map<String, String> mapParam, ICMHttpFile iHttpToolFile, Object objectTag, ICMHttpResult iHttpToolResult) {

    }

    public void onDownLoadComplete(String strURL, Map<String, String> mapParam, ICMHttpFile iHttpToolFile, Object objectTag, ICMHttpResult iHttpToolResult) {

    }
}
