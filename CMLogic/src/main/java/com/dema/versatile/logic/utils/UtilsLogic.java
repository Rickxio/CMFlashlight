package com.dema.versatile.logic.utils;

import android.app.Application;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.tool.AppStatusTool;
import com.dema.versatile.lib.tool.CrashHandler;
import com.dema.versatile.lib.tool.ReferrerReceiver;
import com.dema.versatile.lib.utils.AliveHelp;
import com.dema.versatile.lib.utils.UtilsEncrypt;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsInstall;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.lib.utils.UtilsNetwork;
import com.dema.versatile.lib.utils.UtilsSystem;
import com.dema.versatile.logic.CMLogicFactory;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.utils.UtilsMediation;
import com.dema.versatile.scene.core.CMSceneFactory;

public class UtilsLogic {
    public static String sDomain = null;
    public static String sKeySecret = null;
    public static String sConfigURL = null;
    public static String sCountryURL = null;
    public static String sLogURL = null;
    public static String sCrashURL = null;
    public static String sChannel = null;
    public static boolean sIsDebug = false;

    public static void init(Application application) {
        if (null == application)
            return;

        AppStatusTool.getInstance().init(application);
        CrashHandler.init(!sIsDebug);
        CMLibFactory.setApplication(application);
        CMMediationFactory.setApplication(application);
        CMSceneFactory.setApplication(application);
        CMLogicFactory.setApplication(application);
        UtilsJson.addFactory(CMLibFactory.getInstance());
        UtilsJson.addFactory(CMMediationFactory.getInstance());
        UtilsJson.addFactory(CMSceneFactory.getInstance());
        UtilsJson.addFactory(CMLogicFactory.getInstance());
        UtilsMediation.init(application);
        UtilsEnv.init(application, sChannel);
        UtilsNetwork.init(sDomain);
        UtilsEncrypt.init(sKeySecret);
        UtilsLog.init(application, !sIsDebug, sIsDebug, sLogURL, sCrashURL, null);
        ReferrerReceiver.init(application);

        if (UtilsSystem.isMainProcess(application)) {
            UtilsInstall.init(application);
            AliveHelp.init(application);
        }
    }
}
