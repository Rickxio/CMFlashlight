package com.dema.versatile.lib.core.in;

public interface ICMTimer extends ICMObj {
    boolean start(long lDelayTime, long lRepeatTime, ICMTimerListener iCMTimerListener);

    void stop();
}
