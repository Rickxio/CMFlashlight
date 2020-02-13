package com.dema.versatile.lib.core.im;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.dema.versatile.lib.CMLibFactory;
import com.dema.versatile.lib.core.in.ICMHttp;
import com.dema.versatile.lib.core.in.ICMHttpResult;
import com.dema.versatile.lib.core.in.ICMLog;
import com.dema.versatile.lib.core.in.ICMThreadPool;
import com.dema.versatile.lib.core.in.ICMThreadPoolListener;
import com.dema.versatile.lib.utils.UtilsEnv;
import com.dema.versatile.lib.utils.UtilsFile;
import com.dema.versatile.lib.utils.UtilsJson;
import com.dema.versatile.lib.utils.UtilsLog;
import com.dema.versatile.lib.utils.UtilsNetwork;
import com.dema.versatile.lib.utils.UtilsTime;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.dema.versatile.lib.utils.UtilsLog.logD;

public class CMLog implements ICMLog {
    private Context mContext = null;
    private ICMThreadPool mICMThreadPool = null;
    private ICMHttp mICMHttp = null;
    private ReadWriteLock mReadWriteLockLog = null;
    private ReadWriteLock mReadWriteLockCrash = null;

    public static final int VALUE_INT_LOG_TYPE = 0x1000;
    public static final int VALUE_INT_CRASH_TYPE = 0x1001;

    public CMLog() {
        mContext = CMLibFactory.getApplication();
        _init();
    }

    private void _init() {
        mICMThreadPool = CMLibFactory.getInstance().createInstance(ICMThreadPool.class);
        mICMHttp = CMLibFactory.getInstance().createInstance(ICMHttp.class);
        mReadWriteLockLog = new ReentrantReadWriteLock();
        mReadWriteLockCrash = new ReentrantReadWriteLock();
    }

    @Override
    public void log(final String strKey1, final String strKey2, final JSONObject jsonObjectContent) {
        if (TextUtils.isEmpty(strKey1))
            return;

        mICMThreadPool.run(new ICMThreadPoolListener() {
            @Override
            public void onRun() {
                JSONObject jsonObject = new JSONObject();
                UtilsJson.JsonSerialization(jsonObject, "key1", strKey1);
                UtilsJson.JsonSerialization(jsonObject, "key2", strKey2);
                UtilsJson.JsonSerialization(jsonObject, "content", jsonObjectContent);
                UtilsJson.JsonSerialization(jsonObject, "network", UtilsNetwork.getNetworkType(mContext));
                UtilsJson.JsonSerialization(jsonObject, "date", UtilsTime.getDateStringYyyyMmDdHhMmSs(System.currentTimeMillis()));
                UtilsJson.JsonSerialization(jsonObject, "time", System.currentTimeMillis());
                UtilsJson.JsonSerialization(jsonObject, "basic", UtilsEnv.getBasicInfo(mContext));
                UtilsJson.JsonSerialization(jsonObject, "ab_test", UtilsEnv.getABTestID());
                UtilsJson.JsonSerialization(jsonObject, "extend", UtilsEnv.getExtendInfo());
                String strData = jsonObject.toString();
                write(VALUE_INT_LOG_TYPE, strData);
            }
        });
    }

    @Override
    public void crash(final Throwable throwable) {
        if (null == throwable)
            return;

        mICMThreadPool.run(new ICMThreadPoolListener() {
            @Override
            public void onRun() {
                StringBuffer sb = new StringBuffer();
                crashes(sb, throwable);

                JSONObject jsonObject = new JSONObject();
                UtilsJson.JsonSerialization(jsonObject, "crash", sb.toString());
                UtilsJson.JsonSerialization(jsonObject, "time", System.currentTimeMillis());
                UtilsJson.JsonSerialization(jsonObject, "basic", UtilsEnv.getBasicInfo(mContext));
                String strData = jsonObject.toString();
                write(VALUE_INT_CRASH_TYPE, strData);
            }
        });
    }

    private void crashes(StringBuffer sb, Throwable throwable) {
        if (null == sb || null == throwable)
            return;

        sb.append(throwable.toString());
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            sb.append(element.toString());
        }

        Throwable t = throwable.getCause();
        if (null == t)
            return;

        crashes(sb, t);
    }

    @Override
    public void send() {
        mICMThreadPool.run(new ICMThreadPoolListener() {
            @Override
            public void onRun() {
                send(VALUE_INT_LOG_TYPE);
                send(VALUE_INT_CRASH_TYPE);
            }
        });
    }

    private void write(int nLogType, String strLog) {
        if (TextUtils.isEmpty(strLog))
            return;

        ReadWriteLock lock = VALUE_INT_LOG_TYPE == nLogType ? mReadWriteLockLog : mReadWriteLockCrash;
        lock.writeLock().lock();

        try {
            FileOutputStream fos = mContext.openFileOutput(VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogPath() : UtilsLog.getCrashPath(), Context.MODE_APPEND);

            do {
                fos.write((strLog + UtilsLog.getSeparator()).getBytes());
                fos.flush();
            } while (false);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void send(final int nLogType) {
        if (!UtilsFile.isExists(UtilsFile.makePath(mContext.getFilesDir().getAbsolutePath(),
                VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogPath() : UtilsLog.getCrashPath())))
            return;

        if (UtilsFile.getSize(UtilsFile.makePath(mContext.getFilesDir().getAbsolutePath(),
                VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogPath() : UtilsLog.getCrashPath())) > 10 * 1000 * 1000) {
            mContext.deleteFile(VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogPath() : UtilsLog.getCrashPath());
            return;
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        long lRequestConfigTime = sp.getLong("send_log_time" + nLogType, 0);
        long lTime = System.currentTimeMillis();
        if (lTime - lRequestConfigTime < UtilsTime.VALUE_LONG_TIME_ONE_MINUTE)
            return;

        ReadWriteLock lock = VALUE_INT_LOG_TYPE == nLogType ? mReadWriteLockLog : mReadWriteLockCrash;
        lock.writeLock().lock();

        try {
            FileInputStream fis = mContext.openFileInput(VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogPath() : UtilsLog.getCrashPath());
            int nReadSize = 0;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((nReadSize = fis.read(buffer)) != -1)
                baos.write(buffer, 0, nReadSize);

            fis.close();
            baos.close();

            Map<String, String> mapParam = new HashMap<String, String>();
            mapParam.put("data", baos.toString());

            ICMHttpResult iCMHttpResult = mICMHttp.requestToBufferByPostSync(UtilsNetwork.getURL(VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogURL() : UtilsLog.getCrashURL()), mapParam, null);
            if (null != iCMHttpResult) {
                if (iCMHttpResult.isSuccess()) {
                    sp.edit().putLong("send_log_time" + nLogType, lTime).apply();
                    mContext.deleteFile(VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogPath() : UtilsLog.getCrashPath());
                } else {
                    String exception = iCMHttpResult.getException();
                    if (!TextUtils.isEmpty(exception) && exception.contains("OutOfMemory")) {
                        mContext.deleteFile(VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogPath() : UtilsLog.getCrashPath());
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            mContext.deleteFile(VALUE_INT_LOG_TYPE == nLogType ? UtilsLog.getLogPath() : UtilsLog.getCrashPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

}
