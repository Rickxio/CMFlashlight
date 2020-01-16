package com.dema.versatile.logic;

import android.content.Context;

import java.util.HashMap;

import com.dema.versatile.lib.core.im.CMFactory;
import com.dema.versatile.lib.core.in.ICMFactory;
import com.dema.versatile.lib.core.in.ICMObj;
import com.dema.versatile.logic.core.config.im.ConfigMgr;
import com.dema.versatile.logic.core.config.in.IConfigMgr;

public class CMLogicFactory extends CMFactory {
    private static ICMFactory sICMFactory = null;
    private static Context sContext = null;

    public static ICMFactory getInstance() {
        if (sICMFactory == null) {
            synchronized (CMLogicFactory.class) {
                if (sICMFactory == null)
                    sICMFactory = new CMLogicFactory();
            }
        }

        return sICMFactory;
    }

    public static void setApplication(Context context) {
        sContext = context;
    }

    public static Context getApplication() {
        return sContext;
    }

    {
        mCMFactoryInterfaceMap = new HashMap<>();
        mCMFactoryInterfaceMap.put(IConfigMgr.class, new CMFactoryImplementMap(new Class<?>[]{
                ConfigMgr.class}, new ICMObj[]{null}));
    }
}
