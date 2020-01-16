package com.dema.versatile.mediation.utils;

import android.content.Context;

import com.dema.versatile.mediation.view.AdActivity;

public class UtilsMediation {
    public static void init(Context context) {
        if (null == context)
            return;

        AdActivity.start(context);
        UtilsAdmob.init(context);
        UtilsFacebook.init(context);
        UtilsInmobi.init(context);
    }
}
