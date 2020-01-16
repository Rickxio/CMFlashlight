package com.dema.versatile.lib.utils;

import android.text.TextUtils;

import java.io.File;

public class UtilsFile {

    public static boolean isExists(String strPath) {
        return !TextUtils.isEmpty(strPath) && new File(strPath).exists();
    }

    public static long getSize(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return 0;

        File file = new File(strPath);
        if (!file.exists())
            return 0;

        if (file.isFile())
            return file.length();

        long lSize = 0;
        try {
            for (File f : file.listFiles()) {
                if (f.isDirectory())
                    lSize += getSize(f.getAbsolutePath());
                else
                    lSize += f.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lSize;
    }

    public static String getExtensionName(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        int nIndex = strPath.lastIndexOf(".");
        if (-1 == nIndex)
            return null;

        return strPath.substring(nIndex + 1).toLowerCase();
    }

    public static String getTargetName(String strPath) {
        if (TextUtils.isEmpty(strPath))
            return null;

        int nIndex = strPath.lastIndexOf("/");
        if (-1 == nIndex)
            return null;

        return strPath.substring(nIndex + 1).toLowerCase();
    }

    public static String makePath(String strPath1, String strPath2) {
        if (TextUtils.isEmpty(strPath1) || TextUtils.isEmpty(strPath2))
            return null;

        if (strPath1.endsWith(File.separator))
            return strPath1 + strPath2;

        return strPath1 + File.separator + strPath2;
    }

}
