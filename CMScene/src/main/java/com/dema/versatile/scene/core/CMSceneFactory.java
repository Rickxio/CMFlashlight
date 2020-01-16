package com.dema.versatile.scene.core;

import android.content.Context;

import java.util.HashMap;

import com.dema.versatile.lib.core.im.CMFactory;
import com.dema.versatile.lib.core.in.ICMFactory;
import com.dema.versatile.lib.core.in.ICMObj;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.scene.core.alert.AlertMgrImpl;
import com.dema.versatile.scene.core.alert.IAlertMgr;
import com.dema.versatile.scene.core.config.ISceneItem;
import com.dema.versatile.scene.core.config.SceneItemImpl;
import com.dema.versatile.scene.core.notification.INotificationMgr;
import com.dema.versatile.scene.core.notification.NotificationMgrImpl;
import com.dema.versatile.scene.core.scene.ISceneMgr;
import com.dema.versatile.scene.core.scene.SceneMgrImpl;
import com.dema.versatile.scene.core.store.ISceneDataStore;
import com.dema.versatile.scene.core.store.SceneDataStore;

public class CMSceneFactory extends CMFactory {
    private static ICMFactory sICMFactory = null;
    private static Context sContext = null;

    public static ICMFactory getInstance() {
        if (sICMFactory == null) {
            synchronized (CMSceneFactory.class) {
                if (sICMFactory == null)
                    sICMFactory = new CMSceneFactory();
            }
        }

        return sICMFactory;
    }

    public static void setApplication(Context context) {
        sContext = context;
        UtilsJson.addFactory(CMSceneFactory.getInstance());
    }

    public static Context getApplication() {
        return sContext;
    }

    {
        mCMFactoryInterfaceMap = new HashMap<>();
        mCMFactoryInterfaceMap.put(ISceneMgr.class, new CMFactoryImplementMap(new Class<?>[]{
                SceneMgrImpl.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(ISceneDataStore.class, new CMFactoryImplementMap(new Class<?>[]{
                SceneDataStore.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(ISceneItem.class, new CMFactoryImplementMap(new Class<?>[]{
                SceneItemImpl.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(INotificationMgr.class, new CMFactoryImplementMap(new Class<?>[]{
                NotificationMgrImpl.class}, new ICMObj[]{null}));
        mCMFactoryInterfaceMap.put(IAlertMgr.class, new CMFactoryImplementMap(new Class<?>[]{
                AlertMgrImpl.class}, new ICMObj[]{null}));
    }
}
