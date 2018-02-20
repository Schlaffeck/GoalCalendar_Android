package com.slamcode.goalcalendar.settings;

import com.slamcode.goalcalendar.planning.DateTime;
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

    boolean getShowPlansProgressNotification();

    void setShowPlansProgressNotification(boolean showPlansProgressNotification);

    DateTime getLastLaunchDateTime();

    void setLastLaunchDateTimeMillis(DateTime lastLaunchTime);

    int getThemeId();

    void setThemeId(int themeId);

    DateTime getOnboardingShownDate();

    void setOnboardingShownDate(DateTime onboardingShownDate);

    void addSettingsChangedListener(SettingsChangedListener listener);

    void removeSettingsChangedListener(SettingsChangedListener listener);

    void clearSettingsChangedListeners();

    interface SettingsChangedListener{

        void settingChanged(String settingId);
    }
}
