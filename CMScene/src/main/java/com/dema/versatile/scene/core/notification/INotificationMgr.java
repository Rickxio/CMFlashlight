package com.dema.versatile.scene.core.notification;

import com.dema.versatile.lib.core.in.ICMMgr;

/**
 * Created by wangyu on 2019/8/30.
 */
public interface INotificationMgr extends ICMMgr {

    boolean showNotification(String scene);

    void cancelNotification(String scene);
}
