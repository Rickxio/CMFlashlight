package com.dema.versatile.lib.core.im;

import android.os.Handler;
import android.os.Looper;

import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;

public class CMTimer2 implements ICMTimer {
    private Handler mHandler = null;
    private Runnable mRunnable = null;
    private ICMTimerListener mICMTimerListener = null;
    private boolean mIsWork = false;
    private long mRepeatTime = 0;

    public CMTimer2() {
        _init();
    }

    private void _init() {
        mHandler = new Handler(Looper.getMainLooper());
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (null != mICMTimerListener)
                    mICMTimerListener.onComplete(mRepeatTime);

                if (0 == mRepeatTime) {
                    clear();
                } else {
                    mHandler.postDelayed(mRunnable, mRepeatTime);
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
        mRepeatTime = lRepeatTime;
        mHandler.postDelayed(mRunnable, lDelayTime);
        return true;
    }

    @Override
    public void stop() {
        if (mIsWork) {
            mHandler.removeCallbacks(mRunnable);
        }
        mHandler.removeCallbacksAndMessages(null);
        clear();
    }

    private void clear() {
        mIsWork = false;
        mICMTimerListener = null;
    }
}
