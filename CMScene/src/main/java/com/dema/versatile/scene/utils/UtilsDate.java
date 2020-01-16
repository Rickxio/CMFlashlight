package com.dema.versatile.scene.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilsDate {

    public static String getTimeDetailStr(long time) {
        SimpleDateFormat sDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        return sDateFormat2.format(new Date(time));
    }

    public static String getHourMinuteStr(long time) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return sDateFormat.format(new Date(time));
    }

    public static String getDayString(long time) {
        SimpleDateFormat sDateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return sDateFormat1.format(new Date(time));
    }

    public static String getDayString(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
        } catch (Exception e) {

        }
        return getDayString(calendar.getTimeInMillis());
    }

    public static long getTimeInMillis(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getTimeInMillisForNow() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getTimeInMillis();
    }

    public static long getTimeInMillisForToday(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(new Date());
        calendar.set(Calendar.YEAR, calendarToday.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendarToday.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendarToday.get(Calendar.DAY_OF_MONTH));
        return calendar.getTimeInMillis();
    }

    public static long getTimeMillisForToday(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getTimeInMillisForNextDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTimeInMillis();
    }

    public static long getTimeInMillisForLastDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTimeInMillis();
    }

    public static int getTotalDayOfMonth(int year, int month) {
        int[] monthDays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 100 != 0 && year % 4 == 0) || (year % 400 == 0)) {
            monthDays[1] = 29;
        }
        return monthDays[month];
    }

    public static String getTimeFormatted(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        return sDateFormat.format(calendar.getTime());
    }

    public static String getHourText(int hourOfDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        SimpleDateFormat sHourFormat = new SimpleDateFormat("HH", Locale.ENGLISH);
        return sHourFormat.format(calendar.getTime());
    }

    public static String getMinuteText(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat sMinuteFormat = new SimpleDateFormat("mm", Locale.ENGLISH);
        return sMinuteFormat.format(calendar.getTime());
    }

    public static int getCurrentHourOfDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

}
