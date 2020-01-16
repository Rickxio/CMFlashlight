package com.dema.versatile.scene;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wangyu on 2019/8/23.
 */
public class SceneConstants {
    public static final String VALUE_STRING_EXTRA_TYPE = "intent_extra_type";
    public static final String VALUE_STRING_EXTRA_SCENE = "intent_extra_scene";

    public static final String VALUE_STRING_NOTIFICATION_TYPE = "notification";
    public static final String VALUE_STRING_PULL_ALERT_TYPE = "pull_alert";
    public static final String VALUE_STRING_TIPS_ALERT_TYPE = "tips_alert";

    @StringDef({
            Trigger.VALUE_STRING_TRIGGER_UNLOCK, Trigger.VALUE_STRING_TRIGGER_ALARM, Trigger.VALUE_STRING_TRIGGER_NETWORK
            , Trigger.VALUE_STRING_TRIGGER_CALL_END, Trigger.VALUE_STRING_TRIGGER_CHARGE_STATE, Trigger.VALUE_STRING_TRIGGER_CHARGE_END
            , Trigger.VALUE_STRING_TRIGGER_APP_INSTALL, Trigger.VALUE_STRING_TRIGGER_APP_UPDATE, Trigger.VALUE_STRING_TRIGGER_UNINSTALL
            , Trigger.VALUE_STRING_TRIGGER_RUN})
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Trigger {//当应用在前台时不触发
        String VALUE_STRING_TRIGGER_RUN = "run";                    //应用复活触发
        String VALUE_STRING_TRIGGER_UNLOCK = "unlock";              //解锁屏幕触发
        String VALUE_STRING_TRIGGER_ALARM = "alarm";                //整点闹钟（亮屏状态下）
        String VALUE_STRING_TRIGGER_NETWORK = "network";            //网络变化（排除有网到无网的变化）
        String VALUE_STRING_TRIGGER_CALL_END = "call_end";          //电话挂断
        String VALUE_STRING_TRIGGER_CHARGE_STATE = "charge_start";  //充电开始
        String VALUE_STRING_TRIGGER_CHARGE_END = "charge_end";      //充电结束
        String VALUE_STRING_TRIGGER_APP_INSTALL = "app_install";    //应用安装
        String VALUE_STRING_TRIGGER_APP_UPDATE = "app_update";      //应用更新
        String VALUE_STRING_TRIGGER_UNINSTALL = "app_uninstall";    //应用卸载
    }

    public static final String ACTION_SPLASH = ".action.splash";

    public static final String VALUE_STRING_ACTION_SCENE_ALARM = "scene_alarm_";
    public static final int VALUE_INT_SCENE_ALARM_REQUEST_CODE = 66;

    public static final String VALUE_STRING_ALERT_AD_SCENE = "scene";

}
