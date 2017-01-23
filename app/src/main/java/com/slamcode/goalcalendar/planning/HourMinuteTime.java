package com.slamcode.goalcalendar.planning;

/**
 * Created by moriasla on 23.01.2017.
 */

public final class HourMinuteTime {

    private int hour;

    private int minute;

    public HourMinuteTime(int hour, int minute) {
        if(hour < 0 || hour > 23)
        {
            throw new IllegalArgumentException("Hour value must be between 0 and 23");
        }

        if(minute < 0 || minute > 59)
        {
            throw new IllegalArgumentException("Hour value must be between 0 and 59");
        }

        this.hour = hour;
        this.minute = minute;
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
