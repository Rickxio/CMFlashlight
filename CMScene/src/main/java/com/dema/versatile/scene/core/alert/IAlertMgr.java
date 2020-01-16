package com.dema.versatile.scene.core.alert;

import com.dema.versatile.lib.core.in.ICMMgr;

/**
 * Created by wangyu on 2019/10/15.
 */
public interface IAlertMgr extends ICMMgr {

    void showAlert(String scene, String trigger, int count);
}
