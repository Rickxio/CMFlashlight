package com.dema.versatile.lib;

import android.content.Context;

import java.util.HashMap;

import com.dema.versatile.lib.core.im.CMABTest;
import com.dema.versatile.lib.core.im.CMFactory;
import com.dema.versatile.lib.core.im.CMHttp;
import com.dema.versatile.lib.core.im.CMHttpResult;
import com.dema.versatile.lib.core.im.CMLog;
import com.dema.versatile.lib.core.im.CMThreadPool;
import com.dema.versatile.lib.core.im.CMTimer;
import com.dema.versatile.lib.core.im.CMTimer2;
import com.dema.versatile.lib.core.im.CMTimer3;
import com.dema.versatile.lib.core.in.ICMABTest;
import com.dema.versatile.lib.core.in.ICMFactory;
import com.dema.versatile.lib.core.in.ICMHttp;
import com.dema.versatile.lib.core.in.ICMHttpResult;
import com.dema.versatile.lib.core.in.ICMLog;
import com.dema.versatile.lib.core.in.ICMObj;
import com.dema.versatile.lib.core.in.ICMThreadPool;
import com.dema.versatile.lib.core.in.ICMTimer;

public class CMLibFactory extends CMFactory {
    private static ICMFactory sICMFactory = null;
    private static Context sContext = null;

    public static ICMFactory getInstance() {
        if (sICMFactory == null) {
            synchronized (CMLibFactory.class) {
                if (sICMFactory == null)
                    sICMFactory = new CMLibFactory();
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
        mCMFactoryInterfaceMap.put(ICMThreadPool.class, new CMFactoryImplementMap(new Class<?>[]{
                CMThreadPool.class
        }, new ICMObj[]{
                null
        }));
        mCMFactoryInterfaceMap.put(ICMTimer.class, new CMFactoryImplementMap(new Class<?>[]{
                CMTimer.class, CMTimer2.class, CMTimer3.class
        }, new ICMObj[]{
                null, null, null
        }));
        mCMFactoryInterfaceMap.put(ICMHttp.class, new CMFactoryImplementMap(new Class<?>[]{
                CMHttp.class
        }, new ICMObj[]{
                null
        }));
        mCMFactoryInterfaceMap.put(ICMHttpResult.class, new CMFactoryImplementMap(new Class<?>[]{
                CMHttpResult.class
        }, new ICMObj[]{
                null
        }));
        mCMFactoryInterfaceMap.put(ICMLog.class, new CMFactoryImplementMap(new Class<?>[]{
                CMLog.class
        }, new ICMObj[]{
                null
        }));
        mCMFactoryInterfaceMap.put(ICMABTest.class, new CMFactoryImplementMap(new Class<?>[]{
                CMABTest.class
        }, new ICMObj[]{
                null
        }));
    }
}
