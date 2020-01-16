package com.dema.versatile.lib.core.im;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dema.versatile.lib.core.in.ICMThreadPool;
import com.dema.versatile.lib.core.in.ICMThreadPoolListener;

public class CMThreadPool implements ICMThreadPool {
    private ThreadPoolExecutor mThreadPoolExecutor = null;
    private Handler mHandler = null;

    private static final long VALUE_LONG_KEEP_ALIVE_TIME = 60L;
    private static final int VALUE_INT_MESSAGE_ID = 0x1000;

    public CMThreadPool() {
        _init();
    }

    private void _init() {
        int coreNum = Runtime.getRuntime().availableProcessors();
        mThreadPoolExecutor = new ThreadPoolExecutor(coreNum * 2, coreNum * 2,
                VALUE_LONG_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (null == msg.obj)
                    return;

                CMThreadPoolObject cmThreadPoolObject = (CMThreadPoolObject) msg.obj;
                if (VALUE_INT_MESSAGE_ID == msg.what) {
                    ICMThreadPoolListener iCMThreadPoolListener = cmThreadPoolObject.mICMThreadPoolListener;
                    if (null != iCMThreadPoolListener)
                        iCMThreadPoolListener.onComplete();
                } else {
                    ICMThreadPoolListener iCMThreadPoolListener = cmThreadPoolObject.mICMThreadPoolListener;
                    msg.obj = cmThreadPoolObject.mObject;
                    if (null != iCMThreadPoolListener)
                        iCMThreadPoolListener.onMessage(msg);
                }
            }
        };
    }

    @Override
    public void run(Runnable runnable) {
        if (null == runnable)
            return;

        mThreadPoolExecutor.execute(runnable);
    }

    @Override
    public void run(final ICMThreadPoolListener iCMThreadPoolListener) {
        if (null == iCMThreadPoolListener)
            return;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                iCMThreadPoolListener.onRun();

                Message msg = new Message();
                msg.what = VALUE_INT_MESSAGE_ID;
                CMThreadPoolObject cmThreadPoolObject = new CMThreadPoolObject();
                cmThreadPoolObject.mICMThreadPoolListener = iCMThreadPoolListener;
                msg.obj = cmThreadPoolObject;
                mHandler.sendMessage(msg);
            }
        };

        mThreadPoolExecutor.execute(runnable);
    }

    @Override
    public void sendMessage(ICMThreadPoolListener iCMThreadPoolListener, Message msg) {
        if (null == msg || VALUE_INT_MESSAGE_ID == msg.what)
            return;

        CMThreadPoolObject cmThreadPoolObject = new CMThreadPoolObject();
        cmThreadPoolObject.mICMThreadPoolListener = iCMThreadPoolListener;
        cmThreadPoolObject.mObject = msg.obj;
        msg.obj = cmThreadPoolObject;
        mHandler.sendMessage(msg);
    }

    @Override
    public void stop(boolean bIsNow) {
        if (bIsNow) {
            mThreadPoolExecutor.shutdownNow();
        } else {
            mThreadPoolExecutor.shutdown();
        }
    }

    class CMThreadPoolObject {
        public ICMThreadPoolListener mICMThreadPoolListener = null;
        public Object mObject = null;
    }
}