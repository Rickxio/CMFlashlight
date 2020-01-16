package com.dema.versatile.scene.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMTimer;
import com.dema.versatile.lib.core.in.ICMTimerListener;
import com.dema.versatile.lib.utils.UtilsApp;
import com.dema.versatile.scene.SceneConstants;
import com.dema.versatile.scene.core.CMSceneFactory;
import com.dema.versatile.scene.core.scene.ISceneMgr;

/**
 * Created by wangyu on 2019/8/23.
 */
public class SceneReceiver extends BroadcastReceiver {
    private static SceneReceiver mReceiver = new SceneReceiver();
    private boolean mIsFirstNetChange = true;

    public static void register(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        try {
            context.registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        try {
            context.registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(getAlarmAction(context));
        try {
            context.registerReceiver(mReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(new PhoneStateListener() {
                private int mLastState;

                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    super.onCallStateChanged(state, phoneNumber);
                    if (mLastState == TelephonyManager.CALL_STATE_OFFHOOK
                            && state == TelephonyManager.CALL_STATE_IDLE) {
                        ICMTimer timer = CMLibFactory.getInstance().createInstance(ICMTimer.class);
                        timer.start(2000, 0, new ICMTimerListener() {
                            @Override
                            public void onComplete(long lRepeatTime) {
                                ISceneMgr sceneMgr = CMSceneFactory.getInstance().createInstance(ISceneMgr.class);
                                sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_CALL_END);
                            }
                        });
                    }
                    mLastState = state;
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception e) {

        }
    }

    public static String getAlarmAction(Context context) {
        String myAppPackageName = UtilsApp.getMyAppPackageName(context);
        return SceneConstants.VALUE_STRING_ACTION_SCENE_ALARM + myAppPackageName;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        ISceneMgr sceneMgr = CMSceneFactory.getInstance().createInstance(ISceneMgr.class);
        //alarm触发
        if (TextUtils.equals(action, getAlarmAction(context))) {
            sceneMgr.invalidateAlarm();
            sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_ALARM);
            return;
        }
        //系统广播触发
        switch (action) {
            case Intent.ACTION_USER_PRESENT:
                new Handler().postDelayed(() -> {
                    sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_UNLOCK);
                }, 2000);
                break;
            case Intent.ACTION_POWER_CONNECTED:
                sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_CHARGE_STATE);
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_CHARGE_END);
                break;
            case Intent.ACTION_PACKAGE_ADDED:
                sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_APP_INSTALL);
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_UNINSTALL);
                break;
            case Intent.ACTION_PACKAGE_REPLACED:
                sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_APP_UPDATE);
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                if (mIsFirstNetChange) {
                    mIsFirstNetChange = false;
                    return;
                }
                try {
                    int netType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, 999);
                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager == null)
                        return;

                    NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();//获取网络的连接情况
                    if (activeNetInfo == null)
                        return;

                    if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
                            && netType == ConnectivityManager.TYPE_WIFI) {
                        sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_NETWORK);
                    } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE
                            && netType == ConnectivityManager.TYPE_MOBILE) {
                        sceneMgr.trigger(SceneConstants.Trigger.VALUE_STRING_TRIGGER_NETWORK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
