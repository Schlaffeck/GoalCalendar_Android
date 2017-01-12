package com.slamcode.goalcalendar.planning;

import java.util.Calendar;
import java.util.Date;
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

    public static String getWeekDayNameShort(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day);
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
    }
}
