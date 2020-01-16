package com.dema.versatile.lib.tool;

import com.dema.versatile.lib.utils.UtilsLog;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final long VALUE_LONG_THREAD_DELAY_TIME = 3000;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        UtilsLog.crash(ex);

        try {
            Thread.sleep(VALUE_LONG_THREAD_DELAY_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public static void init(boolean bIsNeedCatch) {
        if (!bIsNeedCatch)
            return;

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }
}