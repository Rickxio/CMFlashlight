package com.dema.versatile.mediation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.lib.utils.UtilsSize;
import com.dema.versatile.lib.utils.UtilsTime;
import com.dema.versatile.mediation.CMMediationFactory;
import com.dema.versatile.mediation.core.in.IMediationMgr;
import com.dema.versatile.mediation.view.AdMaskLayout;

public class UtilsAd {
    public static JSONObject getBaseAdLogJsonObject(String strAdKey, String strAdID, String strAdRequestID, String strAdType, String strAdAction) {
        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "key", strAdKey);
        UtilsJson.JsonSerialization(jsonObject, "action", strAdAction);
        UtilsJson.JsonSerialization(jsonObject, "type", strAdType);
        UtilsJson.JsonSerialization(jsonObject, "ad_id", strAdID);
        UtilsJson.JsonSerialization(jsonObject, "request_id", strAdRequestID);
        return jsonObject;
    }

    public static void log(String strPlatform, JSONObject jsonObject) {
        UtilsLog.log("ad", strPlatform, jsonObject);
    }

    public static void logE(String strTag, String strLog) {
        UtilsLog.logE(strTag,strLog);
    }

    public static boolean showAdView(Context context, View adView, ViewGroup VGContainer, boolean bIsNeedMask) {
        if (null == context || null == adView || null == VGContainer)
            return false;

        ViewGroup VGParent = (ViewGroup) adView.getParent();
        if (null != VGParent) {
            VGParent.removeView(adView);
        }

        AdMaskLayout adMaskLayout = new AdMaskLayout(context);
        if (VGContainer instanceof FrameLayout) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            adMaskLayout.setLayoutParams(layoutParams);
        } else if (VGContainer instanceof RelativeLayout) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            adMaskLayout.setLayoutParams(params);
        }

        if (adView.getTag() instanceof Size) {
            Size size = (Size) adView.getTag();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(UtilsSize.dpToPx(context, size.getWidth()),
                    UtilsSize.dpToPx(context, size.getHeight()));
            lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
            adView.setLayoutParams(lp);
        } else {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER | Gravity.BOTTOM;
            adView.setLayoutParams(lp);
        }
        JSONObject jo = new JSONObject();
        UtilsJson.JsonSerialization(jo, "key", adView.getHeight());
        UtilsLog.log("spam", "ad_addview", jo);
        adMaskLayout.addView(adView);
        adMaskLayout.setInterceptTouchEvent(bIsNeedMask);
        VGContainer.removeAllViews();
        VGContainer.addView(adMaskLayout);
        return true;
    }

    public static boolean isSpamUser(Context context, String strAdKey, String strAdPlatform, String strAdID, String strAdType) {
        if (null == context || TextUtils.isEmpty(strAdKey) || TextUtils.isEmpty(strAdPlatform) || TextUtils.isEmpty(strAdID) || TextUtils.isEmpty(strAdType))
            return true;

        String strTimeKey = "spam_" + strAdPlatform + "_clicktime";
        String strAllClickKey = "spam_all_" + strAdPlatform + "_click";
//        String strClickKey = "spam_" + strAdPlatform + "_click";
        String strAdIDTimeKey = "spam_" + strAdKey+strAdID + "_time";
        String key_interval = "clicktime_interval";
        String key_count = "clickCount";



        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lTime = System.currentTimeMillis();
        long lLastClickTime = sp.getLong(strTimeKey, 0);
//        int nClickCount = sp.getInt(strClickKey, 0);
        long lLastAdIDTime = sp.getLong(strAdIDTimeKey, 0);
        int nAllClickCount = sp.getInt(strAllClickKey, 0);

        long mclicktime_interval = sp.getLong(key_interval, 3600000);
        int mclickCount = sp.getInt(key_count, 2);

        long adtime = lTime - lLastAdIDTime;
        long clicktime_interval = lTime - lLastClickTime;


        JSONObject jo = new JSONObject();
        UtilsJson.JsonSerialization(jo, "key", strAdKey);
        UtilsJson.JsonSerialization(jo, "type", strAdType);
        UtilsJson.JsonSerialization(jo, "ad_id", strAdID);
        UtilsJson.JsonSerialization(jo, "lTime - lLastAdIDTime ", adtime+" = "+lTime+"-"+lLastAdIDTime);
        UtilsJson.JsonSerialization(jo, "minterval", mclicktime_interval);
        UtilsJson.JsonSerialization(jo, "mcount", mclickCount);
        UtilsJson.JsonSerialization(jo, " Thread ",  Thread.currentThread().getId());
        UtilsLog.log("spam", strAdPlatform, jo);

        int screenWidth = UtilsSize.getScreenWidth(context);
        int screenHeight = UtilsSize.getScreenHeight(context);
        JSONObject screen = new JSONObject();

        //屏蔽无效流量，避免view_mian广告挡住首页按钮
        if((strAdKey.equals("view_main") || strAdKey.equals("view_alert"))&& (screenHeight == 0 || screenHeight/screenWidth < 15/9)){
            UtilsJson.JsonSerialization(screen, "request_fail", "Screen match :"+screenHeight+" : "+ screenWidth);
            UtilsLog.log("spam", strAdPlatform, screen);
            return true;
        }

        //屏蔽无效流量，相同广告id请求频率不能小于6s
        if(adtime < 6000){
            UtilsJson.JsonSerialization(screen, "request_fail", "adtime < 6s :"+adtime);
            UtilsLog.log("spam", strAdPlatform, screen);
            return true;
        }
        //屏蔽无效流量，广告点击时间间隔不能小于1H
        if(clicktime_interval < mclicktime_interval){
            UtilsJson.JsonSerialization(screen, "request_fail", "clicktime_interval too short :"+clicktime_interval);
            UtilsLog.log("spam", strAdPlatform, screen);
            return true;
        }
        //屏蔽无效流量，1天内广告点击次数不能超过3次
        if (nAllClickCount >= mclickCount) {
            if (clicktime_interval <= UtilsTime.VALUE_LONG_TIME_ONE_DAY) {
                JSONObject jsonObject = new JSONObject();
                UtilsJson.JsonSerialization(jsonObject, "key", strAdKey);
                UtilsJson.JsonSerialization(jsonObject, "type", strAdType);
                UtilsJson.JsonSerialization(jsonObject, "ad_id", strAdID);
                UtilsJson.JsonSerialization(jsonObject, "request_fail", "nClickCount exceed :"+nAllClickCount);
                UtilsLog.log("spam", strAdPlatform, jsonObject);
                return true;
            } else {
                sp.edit().putInt(strAllClickKey, 0).apply();
            }
        }
        sp.edit().putLong(strAdIDTimeKey, lTime).apply();

        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "is_spam", false);
        UtilsJson.JsonSerialization(jsonObject, "key", strAdKey);
        UtilsJson.JsonSerialization(jsonObject, "type", strAdType);
        UtilsJson.JsonSerialization(jsonObject, "ad_id", strAdID);
        UtilsLog.log("spam", strAdPlatform, jsonObject);
        return false;
    }

    public static synchronized void updateSpamUser(Context context, String strAdKey, String strAdPlatform, String strAdID, String strAdType) {
        if (null == context || TextUtils.isEmpty(strAdKey) || TextUtils.isEmpty(strAdPlatform) || TextUtils.isEmpty(strAdID) || TextUtils.isEmpty(strAdType))
            return;

        String strTimeKey = "spam_" + strAdPlatform + "_clicktime";
        // strClickKey = "spam_" + strAdPlatform + "_click";
        String strAllClickKey = "spam_all_" + strAdPlatform + "_click";

        String key_interval = "clicktime_interval";
        String key_count = "clickCount";

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lTime = System.currentTimeMillis();
        long lLastTime = sp.getLong(strTimeKey, 0);
        int nAllClickCount = sp.getInt(strAllClickKey, 0);
        long mclicktime_interval = sp.getLong(key_interval, 3600000);
        int mclickCount = sp.getInt(key_count, 2);

        /*if (lTime < lLastTime || 0 == lLastTime) {
            lLastTime = lTime;
            sp.edit().putLong(strTimeKey, lTime).apply();
        }*/
        int allClickcount = nAllClickCount + 1;
        long time_interval = lTime - lLastTime;

        /*if ((lTime - lLastTime > UtilsTime.VALUE_LONG_TIME_ONE_MINUTE) && (lTime - lLastTime < UtilsTime.VALUE_LONG_TIME_ONE_DAY) && nClickCount < 3) {
            JSONObject jsonObject = new JSONObject();
            UtilsJson.JsonSerialization(jsonObject, "time", lTime);
            UtilsJson.JsonSerialization(jsonObject, "click", 1);
            UtilsJson.JsonSerialization(jsonObject, "key", strAdKey);
            UtilsJson.JsonSerialization(jsonObject, "type", strAdType);
            UtilsJson.JsonSerialization(jsonObject, "ad_id", strAdID);
            UtilsLog.log("spam", strAdPlatform, jsonObject);

            sp.edit().putLong(strTimeKey, lTime).apply();
            sp.edit().putInt(strClickKey, 1).apply();
            sp.edit().putInt(strAllClickKey,allClickcount ).apply();
            return;
        }
        //        sp.edit().putInt(strClickKey, clickCount).apply();
        */
        JSONObject jsonObject = new JSONObject();
        if(time_interval <= mclicktime_interval || allClickcount >= mclickCount){
            IMediationMgr mIMediationMgr = CMMediationFactory.getInstance().createInstance(IMediationMgr.class);
            mIMediationMgr.releaseAd(strAdKey);
            UtilsJson.JsonSerialization(jsonObject, "ad_click", "limit");
        }

        sp.edit().putInt(strAllClickKey,allClickcount ).apply();
        sp.edit().putLong(strTimeKey, lTime).apply();
//        int clickCount = nClickCount + 1;

        UtilsJson.JsonSerialization(jsonObject, "time", lTime);
//        UtilsJson.JsonSerialization(jsonObject, "click", clickCount);
        UtilsJson.JsonSerialization(jsonObject, "key", strAdKey);
        UtilsJson.JsonSerialization(jsonObject, "type", strAdType);
        UtilsJson.JsonSerialization(jsonObject, "ad_id", strAdID);
        UtilsJson.JsonSerialization(jsonObject, "allClickcount", allClickcount);
        UtilsJson.JsonSerialization(jsonObject, "time_interval", time_interval);
        UtilsLog.log("spam", strAdPlatform, jsonObject);

    }
}
