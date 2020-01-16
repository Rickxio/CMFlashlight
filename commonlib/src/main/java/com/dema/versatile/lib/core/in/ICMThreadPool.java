package com.dema.versatile.lib.core.in;

import android.os.Message;

public interface ICMThreadPool extends ICMMgr {
    void run(Runnable runnable);

    void run(ICMThreadPoolListener iCMThreadPoolListener);

    void sendMessage(ICMThreadPoolListener iCMThreadPoolListener, Message msg);

    void stop(boolean bIsNow);
}
