package com.slamcode.goalcalendar.planning;

/**
 * Interface for all components listening to changes to system date and time
 */

public interface DateTimeChangeListener {

    void onDayChanged();
}
