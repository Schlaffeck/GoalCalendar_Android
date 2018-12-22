package com.slamcode.goalcalendar.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.HourMinuteTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by moriasla on 23.01.2017.
 */

public class SharedPreferencesSettingsManager implements AppSettingsManager, SharedPreferences.OnSharedPreferenceChangeListener {

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

    private final static String LAST_LAUNCH_DATETIME_MILLIS_NAME = "LAST_LAUNCH_DATETIME_MILLIS";
    private final static long LAST_LAUNCH_DATETIME_MILLIS_VALUE = 0;

    private final static String THEME_ID_INDEX_VALUE = "0";
    private final static int[] THEME_ID_ARRAY = new int[]
            {
                    R.style.MaterialTheme,
                    R.style.FlatBrandTheme
            };

    private final static String ONBOARDING_SHOWN_DATETIME_MILLIS_NAME = "ONBOARDING_SHOWN_DATETIME_MILLIS";
    private final static long ONBOARDING_SHOWN_DATETIME_MILLIS_VALUE = 0;

    private SharedPreferences sharedPreferences;

    private List<SettingsChangedListener> settingsChangedListeners = new ArrayList<>();

    public SharedPreferencesSettingsManager(Context context)
    {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean getShowEndOfDayNotification() {
        return this.sharedPreferences.getBoolean(SHOW_EOD_NOTIFICATIONS_SETTING_NAME, SHOW_EOD_NOTIFICATIONS_DEFAULT_VALUE);
    }

    @Override
    public void setShowEndOfDayNotification(boolean showEndOfDayNotification) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(SHOW_EOD_NOTIFICATIONS_SETTING_NAME, showEndOfDayNotification);
        editor.apply();
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
        editor.apply();
    }

    @Override
    public boolean getShowStartupNotification() {
        return this.sharedPreferences.getBoolean(SHOW_STARTUP_NOTIFICATIONS_SETTING_NAME, SHOW_STARTUP_NOTIFICATIONS_DEFAULT_VALUE);
    }

    @Override
    public void setShowStartupNotification(boolean showStartupNotification) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(SHOW_STARTUP_NOTIFICATIONS_SETTING_NAME, showStartupNotification);
        editor.apply();
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
        editor.apply();
    }

    @Override
    public DateTime getLastLaunchDateTime()
    {
        long timeMillis = this.sharedPreferences.getLong(LAST_LAUNCH_DATETIME_MILLIS_NAME, LAST_LAUNCH_DATETIME_MILLIS_VALUE);

        if(timeMillis == 0)
            return null;

        return DateTimeHelper.getDateTime(timeMillis);
    }

    @Override
    public void setLastLaunchDateTimeMillis(DateTime lastLaunchTime) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putLong(LAST_LAUNCH_DATETIME_MILLIS_NAME, lastLaunchTime.getTimeMillis());
        editor.apply();
    }

    @Override
    public int getThemeId() {
        int index = Integer.parseInt(this.sharedPreferences.getString(SettingsKeys.THEME_ID_NAME, THEME_ID_INDEX_VALUE));
        if(THEME_ID_ARRAY.length <= index)
            index = 0;

        return THEME_ID_ARRAY[index];
    }

    @Override
    public void setThemeId(int themeId) {
        int foundIndex = -1;
        for(int i =0 ; i < THEME_ID_ARRAY.length && foundIndex < 0; i++) {
            if(THEME_ID_ARRAY[i] == themeId)
                foundIndex = i;
        }

        if(foundIndex >= 0) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString(SettingsKeys.THEME_ID_NAME, Integer.toString(foundIndex));
        }
    }

    @Override
    public DateTime getOnboardingShownDate() {
        long timeMillis = this.sharedPreferences.getLong(ONBOARDING_SHOWN_DATETIME_MILLIS_NAME, ONBOARDING_SHOWN_DATETIME_MILLIS_VALUE);

        if(timeMillis == 0)
            return null;

        return DateTimeHelper.getDateTime(timeMillis);
    }

    @Override
    public void setOnboardingShownDate(DateTime onboardingShownDate) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putLong(ONBOARDING_SHOWN_DATETIME_MILLIS_NAME, onboardingShownDate.getTimeMillis());
        editor.apply();
    }

    @Override
    public String getUserLocalIdentifier() {
        return this.sharedPreferences.getString(SettingsKeys.LOCAL_USER_IDENTIFIER, null);
    }

    @Override
    public void setUserLocalIdentifier(String userLocalIdentifier) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(SettingsKeys.LOCAL_USER_IDENTIFIER, userLocalIdentifier);
        editor.apply();
    }

    public void addSettingsChangedListener(SettingsChangedListener listener)
    {
        this.settingsChangedListeners.add(listener);
    }

    public void removeSettingsChangedListener(SettingsChangedListener listener)
    {
        this.settingsChangedListeners.remove(listener);
    }

    public void clearSettingsChangedListeners()
    {
        this.settingsChangedListeners.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        notifyPreferenceChanged(key);
    }

    private void notifyPreferenceChanged(String preferenceKey)
    {
        for (SettingsChangedListener listener : this.settingsChangedListeners)
            listener.settingChanged(preferenceKey);
    }
}
