package com.slamcode.goalcalendar.settings;

import com.slamcode.goalcalendar.planning.HourMinuteTime;

import java.util.Date;

/**
 * Created by moriasla on 23.01.2017.
 */

public interface AppSettingsManager {

    boolean getShowEndOfDayNotification();

    void setShowEndOfDayNotification(boolean showEndOfDayNotification);

    HourMinuteTime getEndOfDayNotificationTime();

    void setEndOfDayNotificationTime(HourMinuteTime newTime);

    boolean getShowStartupNotification();

    void setShowStartupNotification(boolean showStartupNotification);

    boolean getAutomaticallyMarkUncompletedTask();
}
