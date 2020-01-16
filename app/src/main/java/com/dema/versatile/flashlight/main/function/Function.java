package com.dema.versatile.flashlight.main.function;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.dema.versatile.flashlight.R;

/**
 * Create by XuChuanting
 * on 2018/8/17-10:34
 * 优化类型
 */
public class Function {


    public static final String TEXT_BOOST = "boost";
    public static final String TEXT_CLEAN = "clean";
    public static final String TEXT_COOL = "cool";
    public static final String TEXT_BATTERY = "battery";
    public static final String TEXT_NETWORK = "network";

    public static final String KEY_FUNCTION_TYPE = "function_type";
    public static final String KEY_FUNCTION_RESULT_TEXT = "function_result_text";

    public static final int TYPE_BOOST = 0;
    public static final int TYPE_CLEAN = 1;
    public static final int TYPE_COOL = 2;
    public static final int TYPE_SAVE_BATTERY = 3;
    public static final int TYPE_NETWORK = 4;

    public static int getTitleRes(@Type int functionType) {
        if (functionType == TYPE_CLEAN) {
            return R.string.clean;
        } else if (functionType == TYPE_BOOST) {
            return R.string.boost;
        } else if (functionType == TYPE_COOL) {
            return R.string.cool;
        } else if (functionType == TYPE_SAVE_BATTERY) {
            return R.string.battery_saver;
        } else {
            return R.string.network_optimization;
        }
    }


    public static int getFunctionIconRes(@Type int functionType) {
        if (functionType == TYPE_CLEAN) {
            return R.drawable.icon_clean_complete;
        } else if (functionType == TYPE_BOOST) {
            return R.drawable.icon_boost_complete;
        } else if (functionType == TYPE_COOL) {
            return R.drawable.icon_cool_complete;
        } else if (functionType == TYPE_SAVE_BATTERY) {
            return R.drawable.icon_battery_save_complete;
        } else {
            return R.drawable.icon_wifi_complete;
        }
    }


    @IntDef(value = {
            TYPE_CLEAN,
            TYPE_BOOST,
            TYPE_COOL,
            TYPE_SAVE_BATTERY,
            TYPE_NETWORK
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

}
