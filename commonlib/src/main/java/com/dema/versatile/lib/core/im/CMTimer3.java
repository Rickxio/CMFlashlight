package com.dema.versatile.lib.core.im;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;

public class CMTimer3 implements ICMTimer {
    private ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor = null;
    private ScheduledFuture<?> mScheduledFuture = null;
    private ICMTimerListener mICMTimerListener = null;
    private Handler mHandler = null;
    private boolean mIsWork = false;

    private static final int VALUE_INT_MESSAGE_TIMER_ID = 0x1000;

    public CMTimer3() {
        _init();
    }

    private void _init() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (VALUE_INT_MESSAGE_TIMER_ID == msg.what) {
                    long lRepeatTime = (Long) msg.obj;
                    if (null != mICMTimerListener)
                        mICMTimerListener.onComplete(lRepeatTime);

                    if (0 == lRepeatTime)
                        clear();
                }
            }
        };
    }

    @Override
    public boolean start(long lDelayTime, long lRepeatTime, ICMTimerListener iCMTimerListener) {
        if (mIsWork || lDelayTime < 0 || lRepeatTime < 0 || null == iCMTimerListener)
            return false;

        mIsWork = true;
        mICMTimerListener = iCMTimerListener;
        mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        if (0 == lRepeatTime) {
            mScheduledFuture = mScheduledThreadPoolExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.what = VALUE_INT_MESSAGE_TIMER_ID;
                    msg.obj = Long.valueOf(lRepeatTime);
                    mHandler.sendMessage(msg);
                }
            }, lDelayTime, TimeUnit.MILLISECONDS);
        } else {
            mScheduledFuture = mScheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.what = VALUE_INT_MESSAGE_TIMER_ID;
                    msg.obj = Long.valueOf(lRepeatTime);
                    mHandler.sendMessage(msg);
                }
            }, lDelayTime, lRepeatTime, TimeUnit.MILLISECONDS);
        }

        return true;
    }

    @Override
    public void stop() {
        if (mIsWork && null != mScheduledFuture && !mScheduledFuture.isCancelled()) {
            mScheduledFuture.cancel(true);
        }
        mHandler.removeCallbacksAndMessages(null);
        clear();
    }

    private void clear() {
        mIsWork = false;
        mICMTimerListener = null;
        mScheduledThreadPoolExecutor = null;
        mScheduledFuture = null;
    }
}
