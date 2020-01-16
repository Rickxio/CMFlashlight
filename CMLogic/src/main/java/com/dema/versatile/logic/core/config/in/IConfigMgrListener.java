package com.dema.versatile.logic.core.config.in;

public interface IConfigMgrListener {
    void onLoadConfigAsyncComplete(boolean bIsLoaded);

    void onRequestConfigAsyncComplete(boolean bIsLoaded);
}
