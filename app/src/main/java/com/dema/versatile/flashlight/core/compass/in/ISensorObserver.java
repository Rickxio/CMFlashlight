package com.dema.versatile.flashlight.core.compass.in;

import android.hardware.SensorEventListener;

import androidx.lifecycle.LifecycleObserver;

public interface ISensorObserver extends LifecycleObserver,SensorEventListener {
    boolean init();

    void setListener(ISensorListener listener);
}
