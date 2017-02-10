package com.slamcode.goalcalendar.planning;

import java.util.Calendar;

/**
 * Class representing easy accessible calendar with basic date time
 * properties access.
 */

public class DateTime {

    private final Calendar calendar;

    public DateTime(Calendar calendar)
    {
        this.calendar = calendar;
    }

    public int getDay(){
        return this.calendar.get(Calendar.DAY_OF_MONTH);
    }

    public Month getMonth(){
        return Month.getMonthByNumber(this.calendar.get(Calendar.MONTH) +1);
    }

    public int getYear()
    {
        return this.calendar.get(Calendar.YEAR);
    }

    public int getHour(){
        return this.calendar.get(Calendar.HOUR);
    }

    public int getMinute(){
        return this.calendar.get(Calendar.MINUTE);
    }

    public int getSecond(){
        return this.calendar.get(Calendar.SECOND);
    }
}
