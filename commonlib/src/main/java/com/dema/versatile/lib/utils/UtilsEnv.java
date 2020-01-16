package com.dema.versatile.lib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;

import com.dema.versatile.lib.BuildConfig;
import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMABTest;

public class UtilsEnv {
    public static int VALUE_INT_BUFFER_SIZE = 4096;
    private static boolean sIsDebuggable = false;
    private static String sChannel = BuildConfig.FLAVOR;
    private static String sPhoneID = null;
    private static String sSimCountry = null;
    private static String sIPCountry = null;
    private static String sReferrer = null;
    private static String sUUID = null;
    private static String sABTestID = null;
    private static JSONObject sJsonObjectExtendInfo = null;

    public static void init(Context context, String strChannel) {
        if (null == context)
            return;

        ApplicationInfo info = context.getApplicationInfo();
        sIsDebuggable = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        sChannel = strChannel;
    }

    public static boolean isDebuggable() {
        return sIsDebuggable;
    }

    public static int getAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getAndroidName() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getAndroidManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    public static String getAndroidBrand() {
        return android.os.Build.BRAND;
    }

    public static String getAndroidModel() {
        return android.os.Build.MODEL;
    }

    public static String getAndroidDevice() {
        return android.os.Build.DEVICE;
    }

    public static int getCPUNumber() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
    }

    public static String getChannel() {
        return sChannel;
    }

    public static String getPhoneID(Context context) {
        if (null == context)
            return null;

        if (!TextUtils.isEmpty(sPhoneID))
            return sPhoneID;

        sPhoneID = getDeviceID(context);
        return sPhoneID;
    }

    public static String getSimCountry(Context context) {
        if (null == context)
            return null;

        if (!TextUtils.isEmpty(sSimCountry))
            return sSimCountry;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        sSimCountry = tm.getSimCountryIso().toUpperCase();
        return sSimCountry;
    }

    public static String getIPCountry(Context context) {
        if (!TextUtils.isEmpty(sIPCountry))
            return sIPCountry;

        if (null == context)
            return null;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sIPCountry = sp.getString("ip_country", null);
        return sIPCountry;
    }

    public static void setIPCountry(Context context, String strIPCountry) {
        if (null == context || TextUtils.isEmpty(strIPCountry))
            return;

        sIPCountry = strIPCountry;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("ip_country", sIPCountry).apply();
    }

    public static String getReferrer() {
        return sReferrer;
    }

    public static void setReferrer(String strReferrer) {
        sReferrer = strReferrer;
    }

    public static String getABTestID() {
        if (!TextUtils.isEmpty(sABTestID))
            return sABTestID;

        ICMABTest iCMABTest = CMLibFactory.getInstance().createInstance(ICMABTest.class);
        sABTestID = iCMABTest.getHitID();
        return sABTestID;
    }

    public static JSONObject getExtendInfo() {
        return sJsonObjectExtendInfo;
    }

    public static void setExtendInfo(JSONObject jsonObjectExtendInfo) {
        sJsonObjectExtendInfo = jsonObjectExtendInfo;
    }

    public static <T extends Object> void addExtendInfo(String strKey, T tValue) {
        if (TextUtils.isEmpty(strKey) || null == tValue)
            return;

        if (null == sJsonObjectExtendInfo) {
            sJsonObjectExtendInfo = new JSONObject();
        }

        UtilsJson.JsonSerialization(sJsonObjectExtendInfo, strKey, tValue);
    }

    public static JSONObject getBasicInfo(Context context) {
        if (null == context)
            return null;

        JSONObject jsonObject = new JSONObject();
        UtilsJson.JsonSerialization(jsonObject, "mid", UtilsEnv.getPhoneID(context));
        UtilsJson.JsonSerialization(jsonObject, "brand", UtilsEnv.getAndroidBrand());
        UtilsJson.JsonSerialization(jsonObject, "model", UtilsEnv.getAndroidModel());
        UtilsJson.JsonSerialization(jsonObject, "sys_version", UtilsEnv.getAndroidVersion());
        UtilsJson.JsonSerialization(jsonObject, "sys_name", UtilsEnv.getAndroidName());
        UtilsJson.JsonSerialization(jsonObject, "app_version", UtilsApp.getMyAppVersionCode(context));
        UtilsJson.JsonSerialization(jsonObject, "app_name", UtilsApp.getMyAppVersionName(context));
        UtilsJson.JsonSerialization(jsonObject, "sim_country", UtilsEnv.getSimCountry(context));
        UtilsJson.JsonSerialization(jsonObject, "ip_country", UtilsEnv.getIPCountry(context));
        UtilsJson.JsonSerialization(jsonObject, "channel", UtilsEnv.getChannel());
        UtilsJson.JsonSerialization(jsonObject, "referrer", UtilsEnv.getReferrer());
        UtilsJson.JsonSerialization(jsonObject, "language", UtilsEnv.getSystemLanguage());
        UtilsJson.JsonSerialization(jsonObject, "screen_w", UtilsSize.getScreenWidth(context));
        UtilsJson.JsonSerialization(jsonObject, "screen_h", UtilsSize.getScreenHeight(context));
        return jsonObject;
    }

    @SuppressLint("HardwareIds")
    private static String getDeviceID(Context context) {
        if (null == context)
            return "unknown";

        String strPhoneID;

        strPhoneID = getAndroidID(context);
        if (!TextUtils.isEmpty(strPhoneID))
            return strPhoneID;

        strPhoneID = getIMEI(context);
        if (!TextUtils.isEmpty(strPhoneID))
            return strPhoneID;

        strPhoneID = android.os.Build.SERIAL;
        if (!TextUtils.isEmpty(strPhoneID))
            return strPhoneID;

        strPhoneID = getUUID(context);
        if (!TextUtils.isEmpty(strPhoneID))
            return strPhoneID;

        return "unknown";
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        String deviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
            if (!TextUtils.isEmpty(deviceId))
                return deviceId;

            deviceId = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(deviceId))
                return deviceId;
        } catch (Exception e) {
        }
        return deviceId;
    }

    public synchronized static String getUUID(Context context) {
        if (null == context)
            return null;

        if (!TextUtils.isEmpty(sUUID))
            return sUUID;

        File installation = new File(context.getFilesDir(), "INSTALLATION");
        try {
            if (!installation.exists()) {
                writeInstallationFile(installation);
            }

            sUUID = readInstallationFile(installation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sUUID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    public static String getUserSerialNumber(Context context) {
        if (null == context)
            return null;

        Object userManager = context.getSystemService(Context.USER_SERVICE);
        if (userManager == null)
            return null;

        try {
            Method methodMyUserHandle = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object objMyUserHandle = methodMyUserHandle.invoke(
                    android.os.Process.class, (Object[]) null);

            Method methodGetSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", objMyUserHandle.getClass());
            long lUserSerial = (Long) methodGetSerialNumberForUser.invoke(userManager, objMyUserHandle);
            return String.valueOf(lUserSerial);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
