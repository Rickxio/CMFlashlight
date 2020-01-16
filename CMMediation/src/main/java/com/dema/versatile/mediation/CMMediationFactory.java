package com.dema.versatile.mediation;

import android.content.Context;

import java.util.HashMap;

import com.dema.versatile.lib.core.im.CMFactory;
import com.dema.versatile.lib.core.in.ICMFactory;
import com.dema.versatile.lib.core.in.ICMObj;
import com.dema.versatile.mediation.core.im.AdItem;
import com.dema.versatile.mediation.core.im.AdmobPlatformMgr;
import com.dema.versatile.mediation.core.im.AdxPlatformMgr;
import com.dema.versatile.mediation.core.im.FacebookPlatformMgr;
import com.dema.versatile.mediation.core.im.InmobiPlatformMgr;
import com.dema.versatile.mediation.core.im.MediationConfig;
import com.dema.versatile.mediation.core.im.MediationMgr;
import com.dema.versatile.mediation.core.im.MopubPlatformMgr;
import com.dema.versatile.mediation.core.in.IAdItem;
import com.dema.versatile.mediation.core.in.IAdPlatformMgr;
import com.dema.versatile.mediation.core.in.IMediationConfig;
import com.dema.versatile.mediation.core.in.IMediationMgr;

public class CMMediationFactory extends CMFactory {
    private static ICMFactory sICMFactory = null;
    private static Context sContext = null;

    public static ICMFactory getInstance() {
        if (sICMFactory == null) {
            synchronized (CMMediationFactory.class) {
                if (sICMFactory == null)
                    sICMFactory = new CMMediationFactory();
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
        mCMFactoryInterfaceMap.put(IMediationMgr.class, new CMFactoryImplementMap(new Class<?>[]{
                MediationMgr.class
        }, new ICMObj[]{
                null
        }));
        mCMFactoryInterfaceMap.put(IMediationConfig.class, new CMFactoryImplementMap(new Class<?>[]{
                MediationConfig.class
        }, new ICMObj[]{
                null
        }));
        mCMFactoryInterfaceMap.put(IAdPlatformMgr.class, new CMFactoryImplementMap(new Class<?>[]{
                FacebookPlatformMgr.class, AdmobPlatformMgr.class, AdxPlatformMgr.class, MopubPlatformMgr.class,
                InmobiPlatformMgr.class
        }, new ICMObj[]{
                null, null, null, null, null
        }));
        mCMFactoryInterfaceMap.put(IAdItem.class, new CMFactoryImplementMap(new Class<?>[]{
                AdItem.class
        }, new ICMObj[]{
                null
        }));
    }
}
