package com.dema.versatile.mediation.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Size;

import com.inmobi.sdk.InMobiSdk;

import org.json.JSONObject;

import com.dema.versatile.mediation.BuildConfig;
import com.dema.versatile.mediation.core.in.IMediationConfig;

public class UtilsInmobi {
    public static void init(Context context) {
        if (null == context)
            return;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String strAccountID = ai.metaData.getString("com.inmobi.ads.ACCOUNT_ID");

            JSONObject jsonObjectConsent = new JSONObject();
            // Provide correct consent value to sdk which is obtained by User
            jsonObjectConsent.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, true);
            // Provide 0 if GDPR is not applicable and 1 if applicable
            jsonObjectConsent.put("gdpr", "0");

            InMobiSdk.init(context, strAccountID, jsonObjectConsent);
            InMobiSdk.setLogLevel(BuildConfig.DEBUG ? InMobiSdk.LogLevel.DEBUG : InMobiSdk.LogLevel.NONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Size getBannerSize(Context context, String strSize) {
        if (null == context || TextUtils.isEmpty(strSize))
            return new Size(320, 50);

        switch (strSize) {
            case IMediationConfig.VALUE_STRING_BANNER_SIZE_LARGE:
                return new Size(300, 250);
            default:
                return new Size(320, 50);
        }
    }
}
