package com.dema.versatile.flashlight.core;

import android.content.Context;

import java.util.HashMap;

import com.dema.versatile.flashlight.core.alarm.AlarmMgrImpl;
import com.dema.versatile.flashlight.core.alarm.IAppAlarmMgr;
import com.dema.versatile.flashlight.core.config.impl.CloudConfig;
import com.dema.versatile.flashlight.core.config.impl.SceneConfig;
import com.dema.versatile.flashlight.core.config.intf.ICloudConfig;
import com.dema.versatile.flashlight.core.config.intf.ISceneConfig;
import com.dema.versatile.flashlight.core.info.IPhoneInfoMgr;
import com.dema.versatile.flashlight.core.info.PhoneInfoMgr;
import com.dema.versatile.flashlight.core.protect.IProtectMgr;
import com.dema.versatile.flashlight.core.protect.OptimizeProtectMgr;
import com.dema.versatile.flashlight.core.settings.ISettingMgr;
import com.dema.versatile.flashlight.core.settings.SettingMgrImpl;
import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMObj;

/**
 * @author jack
 * Create on 2019/3/25 10:29
 */
public class CoreFactory extends CMLibFactory {

    private static Context sContext = null;

    private static CoreFactory sInstance = null;

    private CoreFactory() {
    }

    public static CoreFactory getInstance() {
        if (sInstance == null) {
            synchronized (CoreFactory.class) {
                if (sInstance == null) {
                    sInstance = new CoreFactory();
                }
            }
        }

        return sInstance;
    }

    public static void setApplication(Context context) {
        sContext = context;
    }

    public static Context getApplication() {
        return sContext;
    }

    {
        mCMFactoryInterfaceMap = new HashMap<>();

        mCMFactoryInterfaceMap.put(IAppAlarmMgr.class, new CMFactoryImplementMap(new Class[]{AlarmMgrImpl.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(IProtectMgr.class, new CMFactoryImplementMap(new Class[]{OptimizeProtectMgr.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(ICloudConfig.class, new CMFactoryImplementMap(new Class[]{CloudConfig.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(ISceneConfig.class, new CMFactoryImplementMap(new Class[]{SceneConfig.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(IPhoneInfoMgr.class, new CMFactoryImplementMap(new Class[]{PhoneInfoMgr.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(ISettingMgr.class, new CMFactoryImplementMap(new Class[]{SettingMgrImpl.class}, new ICMObj[]{null}));

    }


}
