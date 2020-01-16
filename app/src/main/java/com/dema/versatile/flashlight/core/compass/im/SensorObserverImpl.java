package com.dema.versatile.flashlight.core.compass.im;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import java.util.List;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.dema.versatile.flashlight.MainApplication;
import com.dema.versatile.flashlight.core.compass.in.ISensorListener;
import com.dema.versatile.flashlight.core.compass.in.ISensorObserver;

public class SensorObserverImpl  implements ISensorObserver {
    private float[] mAccelerometerValues = new float[3];
    private float[] mMagneticFieldValues = new float[3];
    private float[] mValues = new float[3];
    private float[] mMatrix = new float[9];
    private final SensorManager mSensorManager;
    private ISensorListener mListener;
    private Sensor mSensorAccelerometer = null;
    private Sensor mSensorMagneticField = null;
    private boolean mInitResult;
    private Sensor mOrientationSensor;//方向传感器
    private boolean mIsSupportOrientationSensor;
    private float mOrientationDegree, mPitchDegree, mRollDegree;
    public static SensorObserverImpl mInstance;

    public SensorObserverImpl() {
        mSensorManager = (SensorManager) MainApplication.getApplication().getSystemService(Context.SENSOR_SERVICE);
    }

    public static SensorObserverImpl getInstance(){
        if (mInstance == null){
            mInstance = new SensorObserverImpl();
        }
        return mInstance;
    }

    @Override
    public boolean init() {
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        boolean hasMagneticSensor = false;
        boolean hasAccelerometerSensor = false;
        mIsSupportOrientationSensor = false;
        if (sensorList != null) {
            for (Sensor sensor : sensorList) {
                if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                    mIsSupportOrientationSensor = true;
                }
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    hasAccelerometerSensor = true;
                    mSensorAccelerometer = sensor;
                }
                if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    hasMagneticSensor = true;
                    mSensorMagneticField = sensor;
                }
            }
        }
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mInitResult = hasMagneticSensor && hasAccelerometerSensor;
        return mInitResult;
    }

    @Override
    public void setListener(ISensorListener listener) {
        mListener = listener;
    }


    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    private void registerSensorEvent() {
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        if (mOrientationSensor != null) {
            mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_GAME);
        }
        if (mSensorMagneticField != null) {
            mSensorManager.registerListener(this, mSensorMagneticField, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    private void unregisterSensorEvent() {
        if (mInitResult) {
            mSensorManager.unregisterListener(this);
        }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    private void registerListener() {
        if (mListener != null) {

        }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    private void unregisterListener() {
        if (mListener != null) {
           mListener = null;
        }
        if (mInitResult) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerValues = event.values.clone();
                calculateOrientation(event);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagneticFieldValues = event.values.clone();
                break;
            case Sensor.TYPE_ORIENTATION:
                mOrientationDegree = event.values[0];
                mPitchDegree = event.values[1];
                mRollDegree = event.values[2];
                calculateOrientation(event);
                break;
        }


    }

    private void calculateOrientation(SensorEvent event) {
        if (!mIsSupportOrientationSensor) {
            //调用getRotationMatrix获得变换矩阵mMatrix[]
            SensorManager.getRotationMatrix(mMatrix, null, mAccelerometerValues, mMagneticFieldValues);
            SensorManager.getOrientation(mMatrix, mValues);

            mOrientationDegree = (float) Math.toDegrees(mValues[0]);
            if (mOrientationDegree < 0) {
                mOrientationDegree += 360;
            }
            mPitchDegree = (float) Math.toDegrees(mValues[1]);
            mRollDegree = -(float) Math.toDegrees(mValues[2]);


        }
        if (mListener != null){
            mListener.onAngelChanged(mOrientationDegree, mPitchDegree, mRollDegree);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
