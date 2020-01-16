package com.dema.versatile.lib.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class UtilsSize {
    public static int dpToPx(Context context, float fDp) {
        if (null == context)
            return 0;

        return Math.round(fDp * context.getResources().getDisplayMetrics().density);
    }

    public static int pxToDp(Context context, float fPx) {
        if (null == context)
            return 0;

        return Math.round(fPx / context.getResources().getDisplayMetrics().density);
    }

    public static int getScreenWidth(Context context) {
        if (null == context)
            return 0;

        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            if (null != dm) {
                return dm.widthPixels;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getScreenHeight(Context context) {
        if (null == context)
            return 0;

        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            if (null != dm) {
                return dm.heightPixels;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
