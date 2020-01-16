package com.dema.versatile.lib.core.in;

import android.os.Message;

public abstract class ICMThreadPoolListener {
    public abstract void onRun();

    public void onComplete() {
    }

    public void onMessage(Message msg) {
    }
}
