package com.dema.versatile.lib.core.im;

import java.util.Map;

import com.dema.versatile.lib.core.in.ICMFactory;
import com.dema.versatile.lib.core.in.ICMMgr;
import com.dema.versatile.lib.core.in.ICMObj;

public abstract class CMFactory implements ICMFactory {
    @Override
    public <T> T createInstance(Class<T> classInterface) {
        return createInstance(classInterface, null);
    }

    @Override
    public <T> T createInstance(Class<T> classInterface, Class<?> classImplement) {
        if (null == classInterface || !ICMObj.class.isAssignableFrom(classInterface))
            return null;

        CMFactoryImplementMap cmFactoryImplementMap = findImplementMap(classInterface);
        if (null == cmFactoryImplementMap)
            return null;

        int nImplementPos = findImplementPos(cmFactoryImplementMap, classImplement);
        if (-1 == nImplementPos)
            return null;

        ICMObj iCMObj = null;
        if (ICMMgr.class.isAssignableFrom(classInterface)) {
            synchronized (CMFactory.class) {
                if (null != cmFactoryImplementMap.mArrayICMObj[nImplementPos]) {
                    iCMObj = cmFactoryImplementMap.mArrayICMObj[nImplementPos];
                } else {
                    try {
                        iCMObj = (ICMObj) cmFactoryImplementMap.mArrayClassImplement[nImplementPos].newInstance();
                        cmFactoryImplementMap.mArrayICMObj[nImplementPos] = iCMObj;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            synchronized (CMFactory.class) {
                try {
                    iCMObj = (ICMObj) cmFactoryImplementMap.mArrayClassImplement[nImplementPos].newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return (T) iCMObj;
    }

    @Override
    public boolean isClassInterfaceExist(Class<?> classInterface) {
        if (null == classInterface)
            return false;

        return mCMFactoryInterfaceMap.containsKey(classInterface);
    }

    private CMFactoryImplementMap findImplementMap(Class<?> classInterface) {
        if (null == classInterface)
            return null;

        return mCMFactoryInterfaceMap.get(classInterface);
    }

    private int findImplementPos(CMFactoryImplementMap cmFactoryImplementMap, Class<?> classImplement) {
        if (null == cmFactoryImplementMap || null == cmFactoryImplementMap.mArrayClassImplement || 0 == cmFactoryImplementMap.mArrayClassImplement.length)
            return -1;

        if (null == classImplement)
            return 0;

        for (int nIndex = 0; nIndex < cmFactoryImplementMap.mArrayClassImplement.length; nIndex++) {
            if (classImplement == cmFactoryImplementMap.mArrayClassImplement[nIndex]) {
                return nIndex;
            }
        }

        return -1;
    }

    protected Map<Class<?>, CMFactoryImplementMap> mCMFactoryInterfaceMap = null;

    protected class CMFactoryImplementMap {
        public Class<?>[] mArrayClassImplement = null;
        public ICMObj[] mArrayICMObj = null;

        public CMFactoryImplementMap(Class<?>[] arrayClassImplement, ICMObj[] arrayICMObj) {
            this.mArrayClassImplement = arrayClassImplement;
            this.mArrayICMObj = arrayICMObj;
        }
    }
}
