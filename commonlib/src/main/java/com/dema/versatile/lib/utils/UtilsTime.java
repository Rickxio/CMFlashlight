package com.dema.versatile.lib.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UtilsTime {
    public static final long VALUE_LONG_TIME_ONE_SECOND = 1000;
    public static final long VALUE_LONG_TIME_ONE_MINUTE = VALUE_LONG_TIME_ONE_SECOND * 60;
    public static final long VALUE_LONG_TIME_ONE_HOUR = VALUE_LONG_TIME_ONE_MINUTE * 60;
    public static final long VALUE_LONG_TIME_ONE_DAY = VALUE_LONG_TIME_ONE_HOUR * 24;


    public static String getDateStringYyyyMmDdHhMmSs(long lTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return simpleDateFormat.format(new Date(lTime));
    }

    public static String getDateStringHh(long lTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.ENGLISH);
        return simpleDateFormat.format(new Date(lTime));
    }
}
