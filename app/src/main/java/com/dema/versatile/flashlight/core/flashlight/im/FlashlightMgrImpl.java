package com.dema.versatile.flashlight.core.flashlight.im;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.dema.versatile.flashlight.MainApplication;
import com.dema.versatile.flashlight.core.flashlight.in.IFlashlightMgr;
import com.dema.versatile.flashlight.core.flashlight.in.IStrobeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FlashlightMgrImpl implements IFlashlightMgr {
    private static FlashlightMgrImpl newInstance;
    private final int VALUE_INT_SOS_SHOT = 11;
    private final int VALUE_INT_SOS_LONG = 12;
    public static final int VALUE_INT_SOS = 11;
    public static final int VALUE_INT_0 = 0;
    public static final int VALUE_INT_1 = 1;
    public static final int VALUE_INT_2 = 2;
    public static final int VALUE_INT_3 = 3;
    public static final int VALUE_INT_4 = 4;
    public static final int VALUE_INT_5 = 5;
    public static final int VALUE_INT_6 = 6;
    public static final int VALUE_INT_7 = 7;
    public static final int VALUE_INT_8 = 8;
    public static final int VALUE_INT_9 = 9;
    public static final int VALUE_INT_10 = 10;
    private final int VALUE_INT_OPEN = 102;
    private final int VALUE_INT_CLOSE = 103;
    private final int VALUE_INT_SOS_LONG_TIME = 1500;
    private final int VALUE_INT_SOS_SHORT_TIME = 500;
    private final int VALUE_INT_SOS_INTERVAL_TIME = 2000;
    private String[] itemStr = {"3",  "5", "4", "0", "1", "2"};
    private boolean mCanOpenFlashlight = false;
    private boolean mLightState = false;
    private int mFlickerState = VALUE_INT_0;
    private Context mContext;
    private CameraManager mCameraManager;
    private Camera mCamera;
    private Handler handler;
    private int mSosCount = 0;
    private int mHandlerSleepTime = 1000;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private int mSosSleepTime = 0;
    private int mLevel = 0;
    private boolean mSwitchState = false;
    private int lastMode;
    private CameraManager.TorchCallback mTorchCallback;
    private CameraManager.AvailabilityCallback mAvailabilityCallback;

    private FlashlightMgrImpl() {
        mContext = MainApplication.getApplication();
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        canOpenFlashlight();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case VALUE_INT_OPEN:
                        openLight();
                        break;
                    case VALUE_INT_CLOSE:
                        closeLight();
                        if (mFlickerState == VALUE_INT_SOS_SHOT || mFlickerState == VALUE_INT_SOS_LONG) {
                            mSosCount++;
                            sos();
                        }
                        break;
                }
                return false;
            }
        });

    }

    public static FlashlightMgrImpl getInstance() {
        if (newInstance == null) {
            newInstance = new FlashlightMgrImpl();
        }
        return newInstance;
    }

    @Override
    public void register() {
        if (Build.VERSION.SDK_INT >= 23) {
            mTorchCallback = new CameraManager.TorchCallback() {
                @Override
                public void onTorchModeUnavailable(@NonNull String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
                }

                @Override
                public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                    super.onTorchModeChanged(cameraId, enabled);
                }
            };
            mAvailabilityCallback = new CameraManager.AvailabilityCallback() {
                @Override
                public void onCameraAvailable(@NonNull String cameraId) {
                    super.onCameraAvailable(cameraId);
                }

                @Override
                public void onCameraUnavailable(@NonNull String cameraId) {
                    super.onCameraUnavailable(cameraId);
                }
            };
            try {
                mCameraManager.registerTorchCallback(mTorchCallback, null);
                mCameraManager.registerAvailabilityCallback(mAvailabilityCallback, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void unregister() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (mTorchCallback != null) {
                    mCameraManager.unregisterTorchCallback(mTorchCallback);
                }
                if (mAvailabilityCallback != null) {
                    mCameraManager.unregisterAvailabilityCallback(mAvailabilityCallback);
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public List<IStrobeItem> initRvList() {

        List<IStrobeItem> list = new ArrayList<>();
        for (int i = 0; i < itemStr.length; i++) {
            IStrobeItem item = new StrobeItemImpl();
            item.setSelect(false);
            item.setText(itemStr[i]);
            list.add(item);
        }
        return list;
    }

    @Override
    public boolean clickSwitch() {
        if (!mCanOpenFlashlight) {
            return false;
        }
        if (mSwitchState) {
            mSwitchState = false;
            cleanTimeTask();
            closeLight();
            mSosCount = 0;
        } else {
            mSwitchState = true;
//            openLight();
            level(mLevel);
        }
        return mSwitchState;

    }

    @Override
    public boolean getLightState() {
        return mLightState;
    }

    @Override
    public void setLightState(boolean b) {
        mLightState = b;
    }

    @Override
    public void changeRVState(int position) {
        mLevel = position;
        if (mSwitchState) {
            level(position);
        }
    }

    @Override
    public void setLevel(int level) {
        mLevel = level;

    }

    @Override
    public int getSelectState() {
        return mFlickerState;
    }

    private void canOpenFlashlight() {
        if (Build.VERSION.SDK_INT >= 23) {
            mCanOpenFlashlight = true;
        } else {
            PackageManager pm = mContext.getPackageManager();
            for (FeatureInfo info : pm.getSystemAvailableFeatures()) {
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(info.name)) {
                    mCanOpenFlashlight = true;
                    break;
                }
            }
        }
    }

    @Override
    public void openLight() {
        try {
            if (mLightState) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    mLightState = true;
                    mCameraManager.setTorchMode("0", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mLightState = true;
                mCamera = Camera.open();
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }
        } catch (Exception e) {
            mLightState = false;
        }
    }

    @Override
    public void closeLight() {
        try {
            if (!mLightState) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    mCameraManager.setTorchMode("0", false);
                    mLightState = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (mCamera != null) {
                    mLightState = false;
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }
        } catch (Exception e) {
            mLightState = true;
        }
    }

    private void level(int level) {

        cleanTimeTask();
        startTask();
        switch (level) {
            case VALUE_INT_0:
                mFlickerState = VALUE_INT_0;
                mSosCount = 0;
                cleanTimeTask();
                handler.sendEmptyMessage(VALUE_INT_OPEN);
                break;
            case VALUE_INT_1:
                mFlickerState = VALUE_INT_1;
                mHandlerSleepTime = 1000;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_2:
                mFlickerState = VALUE_INT_2;
                mHandlerSleepTime = 900;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_3:
                mFlickerState = VALUE_INT_3;
                mHandlerSleepTime = 800;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_4:
                mFlickerState = VALUE_INT_4;
                mHandlerSleepTime = 700;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_5:
                mFlickerState = VALUE_INT_5;
                mHandlerSleepTime = 600;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_6:
                mFlickerState = VALUE_INT_6;
                mHandlerSleepTime = 500;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_7:
                mFlickerState = VALUE_INT_7;
                mHandlerSleepTime = 400;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_8:
                mFlickerState = VALUE_INT_8;
                mHandlerSleepTime = 300;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_9:
                mFlickerState = VALUE_INT_9;
                mHandlerSleepTime = 200;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_10:
                mFlickerState = VALUE_INT_10;
                mHandlerSleepTime = 100;
                mSosCount = 0;
                mTimer.schedule(mTimerTask, 0, mHandlerSleepTime);
                break;
            case VALUE_INT_SOS_SHOT:
                mFlickerState = VALUE_INT_SOS_SHOT;
                mHandlerSleepTime = VALUE_INT_SOS_SHORT_TIME;
                mSosSleepTime = VALUE_INT_SOS_SHORT_TIME;
                if (mSosCount == 0 && lastMode == VALUE_INT_SOS_SHOT) {//如果上一次是sos三短模式，则说明运行完成一组，下一组开始间隔时间加长
                    mSosSleepTime = VALUE_INT_SOS_INTERVAL_TIME;
                } else if (mSosCount == 0 && (lastMode != VALUE_INT_SOS_SHOT || lastMode != VALUE_INT_SOS_LONG) && mSwitchState) {
                    closeLight();
                }
                mTimer.schedule(mTimerTask, mSosSleepTime, mHandlerSleepTime);
                break;
            case VALUE_INT_SOS_LONG:
                mFlickerState = VALUE_INT_SOS_LONG;
                mSosSleepTime = VALUE_INT_SOS_SHORT_TIME;
                mHandlerSleepTime = VALUE_INT_SOS_LONG_TIME;
                mTimer.schedule(mTimerTask, mSosSleepTime, mHandlerSleepTime);
                break;
            default:
                handler.sendEmptyMessage(VALUE_INT_CLOSE);
                break;
        }
        lastMode = level;
    }

    @Override
    public void cleanTimeTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public boolean isOpenSos() {
        return mLevel == VALUE_INT_SOS_SHOT;
    }

    public void startTask() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mLightState) {
                    handler.sendEmptyMessage(VALUE_INT_CLOSE);
                } else {
                    handler.sendEmptyMessage(VALUE_INT_OPEN);
                }
            }
        };
    }

    private void sos() {
        if (mFlickerState == VALUE_INT_SOS_SHOT || mFlickerState == VALUE_INT_SOS_LONG) {
            if (mSosCount == 3) {
                level(VALUE_INT_SOS_LONG);
            } else if (mSosCount == 6) {
                level(VALUE_INT_SOS_SHOT);
            } else if (mSosCount == 9) {
                mSosCount = 0;
                level(VALUE_INT_SOS_SHOT);
            }
        }
    }

    @Override
    public void destroy() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public String[] getItemString() {
        return itemStr;
    }

    @Override
    public boolean getSwitchState() {
        return mSwitchState;
    }

    @Override
    public void setSwitchState(boolean state) {
        mSwitchState = state;
    }


}
