package com.slamcode.goalcalendar.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.slamcode.goalcalendar.planning.HourMinuteTime;

/**
 * Created by moriasla on 23.01.2017.
 */

public class SharedPreferencesSettingsManager implements AppSettingsManager {

    // keep below values compatible with the preferences activity resources
    private final static int EOD_NOTIFICATION_HOUR_TIME_DEFAULT_VALUE = 20;
    private final static int EOD_NOTIFICATION_MINUTE_TIME_DEFAULT_VALUE = 0;

    private final static String EOD_NOTIFICATION_TIME_HOUR_SETTING_NAME = "EOD_NOTIFICATION_TIME_HOUR";
    private final static String EOD_NOTIFICATION_TIME_MINUTE_SETTING_NAME = "EOD_NOTIFICATION_MINUTE_HOUR";

    private final static String SHOW_STARTUP_NOTIFICATIONS_SETTING_NAME = "SHOW_STARTUP_NOTIFICATIONS";
    private final static boolean SHOW_STARTUP_NOTIFICATIONS_DEFAULT_VALUE = true;

    private final static String SHOW_EOD_NOTIFICATIONS_SETTING_NAME = "SHOW_EOD_NOTIFICATIONS";
    private final static boolean SHOW_EOD_NOTIFICATIONS_DEFAULT_VALUE = true;

    private final static String SHOW_PLANS_PROGRESS_NOTIFICATIONS_SETTING_NAME = "SHOW_PLANS_PROGRESS_NOTIFICATIONS";
    private final static boolean SHOW_PLANS_PROGRESS_NOTIFICATIONS_DEFAULT_VALUE = true;

    private final static String MARK_UNCOMPLETED_AS_FAILED_NAME = "MARK_UNCOMPLETED_AS_FAILED";
    private final static boolean MARK_UNCOMPLETED_AS_FAILED_VALUE = true;

    private final Context context;

    private SharedPreferences sharedPreferences;

    public SharedPreferencesSettingsManager(Context context)
    {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean getShowEndOfDayNotification() {
        return this.sharedPreferences.getBoolean(SHOW_EOD_NOTIFICATIONS_SETTING_NAME, SHOW_EOD_NOTIFICATIONS_DEFAULT_VALUE);
    }

    @Override
    public void setShowEndOfDayNotification(boolean showEndOfDayNotification) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(SHOW_EOD_NOTIFICATIONS_SETTING_NAME, showEndOfDayNotification);
        editor.commit();
    }

    @Override
    public HourMinuteTime getEndOfDayNotificationTime() {
        return new HourMinuteTime(
                this.sharedPreferences.getInt(EOD_NOTIFICATION_TIME_HOUR_SETTING_NAME, EOD_NOTIFICATION_HOUR_TIME_DEFAULT_VALUE),
                this.sharedPreferences.getInt(EOD_NOTIFICATION_TIME_MINUTE_SETTING_NAME, EOD_NOTIFICATION_MINUTE_TIME_DEFAULT_VALUE));
    }

    @Override
    public void setEndOfDayNotificationTime(HourMinuteTime newTime) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putInt(EOD_NOTIFICATION_TIME_HOUR_SETTING_NAME, newTime.getHour());
        editor.putInt(EOD_NOTIFICATION_TIME_MINUTE_SETTING_NAME, newTime.getMinute());
        editor.commit();
    }

    @Override
    public boolean getShowStartupNotification() {
        return this.sharedPreferences.getBoolean(SHOW_STARTUP_NOTIFICATIONS_SETTING_NAME, SHOW_STARTUP_NOTIFICATIONS_DEFAULT_VALUE);
    }

    @Override
    public void setShowStartupNotification(boolean showStartupNotification) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(SHOW_STARTUP_NOTIFICATIONS_SETTING_NAME, showStartupNotification);
        editor.commit();
    }

    @Override
    public boolean getAutomaticallyMarkUncompletedTask() {
        return this.sharedPreferences.getBoolean(MARK_UNCOMPLETED_AS_FAILED_NAME, MARK_UNCOMPLETED_AS_FAILED_VALUE);
    }

    @Override
    public boolean getShowPlansProgressNotification() {
        return this.sharedPreferences.getBoolean(SHOW_PLANS_PROGRESS_NOTIFICATIONS_SETTING_NAME, SHOW_PLANS_PROGRESS_NOTIFICATIONS_DEFAULT_VALUE);
    }

    @Override
    public void setShowPlansProgressNotification(boolean showPlansProgressNotification) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(SHOW_PLANS_PROGRESS_NOTIFICATIONS_SETTING_NAME, showPlansProgressNotification);
        editor.commit();
    }
}
