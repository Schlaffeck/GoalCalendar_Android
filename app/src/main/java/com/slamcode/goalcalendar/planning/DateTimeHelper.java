package com.slamcode.goalcalendar.planning;

import android.os.SystemClock;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by moriasla on 09.01.2017.
 */

public final class DateTimeHelper {

    public static int getCurrentYear()
    {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date());

        return calendar.get(Calendar.YEAR);
    }

    public static Month getCurrentMonth()
    {
        return Month.getCurrentMonth();
    }

    public static boolean isCurrentDate(int year, int month, int day)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        if((cal.get(Calendar.YEAR)) != year)
        {
            return false;
        }

        if((cal.get(Calendar.MONTH)) != month-1)
        {
            return false;
        }

        if((cal.get(Calendar.DAY_OF_MONTH)) != day)
        {
            return false;
        }

        return true;
    }

    public static boolean isCurrentDate(int year, Month month, int day)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        if((cal.get(Calendar.YEAR)) != year)
        {
            return false;
        }

        if((cal.get(Calendar.MONTH)) != month.getNumValue()-1)
        {
            return false;
        }

        if((cal.get(Calendar.DAY_OF_MONTH)) != day)
        {
            return false;
        }

        return true;
    }

    public static String getWeekDayNameShort(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day);
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
    }

    public static int currentDayNumber() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar getTodayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar getYesterdayCalendar() {
        Calendar calendar = getTodayCalendar();

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar;
    }

    public static DateTime getYesterdayDateTime() {
        return new DateTime(getYesterdayCalendar());
    }

    public static Calendar getTomorrowCalendar() {
        Calendar calendar = getTodayCalendar();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    public static Calendar getNowCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar;
    }

    public static Calendar getTodayCalendar(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static long getDiffTimeMillis(Calendar fromTime, Calendar toTime)
    {
        return toTime.getTimeInMillis() - fromTime.getTimeInMillis();
    }

    public static int getDaysCount(int year, Month month) {

        switch (month)
        {
            case JANUARY:
            case MARCH:
            case MAY:
            case JULY:
            case AUGUST:
            case OCTOBER:
            case DECEMBER:
                return 31;
            case FEBRUARY: return isLeapYear(year) ? 29 : 28;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                return 30;
        }

        return 0;
    }

    public static boolean isLeapYear(int year)
    {
        return ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
    }
}
