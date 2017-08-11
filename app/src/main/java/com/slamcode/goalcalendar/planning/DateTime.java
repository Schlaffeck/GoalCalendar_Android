package com.slamcode.goalcalendar.planning;

import java.util.Calendar;
import java.util.Locale;

/**
 * Class representing easy accessible calendar with basic date time
 * properties access.
 */

public class DateTime {

    private final Calendar calendar;
    private int day;
    private Month month;
    private int year;
    private int hour;
    private int minute;
    private int second;

    public DateTime(Calendar calendar)
    {
        this.calendar = calendar;
        this.day = this.calendar.get(Calendar.DAY_OF_MONTH);
        this.month = Month.getMonthByNumber(this.calendar.get(Calendar.MONTH) +1);
        this.year = this.calendar.get(Calendar.YEAR);
        this.hour = this.calendar.get(Calendar.HOUR);
        this.minute = this.calendar.get(Calendar.MINUTE);
        this.second = this.calendar.get(Calendar.SECOND);
    }

    public DateTime(int year, Month month, int day)
    {
        this(DateTimeHelper.getCalendar(year, month, day));
    }

    public int getDay(){
        return this.day;
    }

    public Month getMonth(){
        return this.month;
    }

    public int getYear()
    {
        return this.year;
    }

    public int getHour(){
        return this.hour;
    }

    public int getMinute(){
        return this.minute;
    }

    public int getSecond(){
        return this.second;
    }

    public long getTimeMillis()
    {
        return this.calendar.getTimeInMillis();
    }

    @Override
    public String toString() {

        return String.format("%d-%d-%d %d:%d:%d", this.day, this.month.getNumValue(), this.year, this.hour, this.minute, this.second);
    }
}
