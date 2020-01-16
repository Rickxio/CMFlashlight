package com.dema.versatile.lib.core.im;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;

public class CMTimer implements ICMTimer {
    private Timer mTimer = null;
    private ICMTimerListener mICMTimerListener = null;
    private Handler mHandler = null;
    private boolean mIsWork = false;

    private static final int VALUE_INT_MESSAGE_TIMER_ID = 0x1000;

    public CMTimer() {
        _init();
    }

    private void _init() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (null == msg.obj)
                    return;

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
    public boolean start(long lDelayTime, final long lRepeatTime, ICMTimerListener iCMTimerListener) {
        if (mIsWork || lDelayTime < 0 || lRepeatTime < 0 || null == iCMTimerListener)
            return false;

        mIsWork = true;
        mICMTimerListener = iCMTimerListener;
        mTimer = new Timer();
        if (0 == lRepeatTime) {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = VALUE_INT_MESSAGE_TIMER_ID;
                    msg.obj = Long.valueOf(lRepeatTime);
                    mHandler.sendMessage(msg);
                }
            }, lDelayTime);
        } else {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = VALUE_INT_MESSAGE_TIMER_ID;
                    msg.obj = Long.valueOf(lRepeatTime);
                    mHandler.sendMessage(msg);
                }
            }, lDelayTime, lRepeatTime);
        }

        return true;
    }

    @Override
    public void stop() {
        if (mIsWork && null != mTimer) {
            mTimer.cancel();
        }

        mHandler.removeCallbacksAndMessages(null);
        clear();
    }

    private void clear() {
        mIsWork = false;
        mICMTimerListener = null;
        mTimer = null;
    }
}
