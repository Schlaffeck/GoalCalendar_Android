package com.slamcode.goalcalendar.service.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.diagniostics.Logger;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.HourMinuteTime;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by moriasla on 19.01.2017.
 */

public final class EndOfDayNotificationProvider implements NotificationProvider {

    private static final String LOG_TAG = "GOAL_EODNotPrv";

    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_ID_STRING = "EndOfDayNotification";

    private final ApplicationContext context;
    private final AppSettingsManager settingsManager;
    private final NotificationHistory notificationHistory;
    private final Logger logger;

    public EndOfDayNotificationProvider(ApplicationContext context, AppSettingsManager settingsManager,
                                        NotificationHistory notificationHistory, Logger logger)
    {
        this.context = context;
        this.settingsManager = settingsManager;
        this.notificationHistory = notificationHistory;
        this.logger = logger;
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public String getNotificationIdString() {
        return NOTIFICATION_ID_STRING;
    }

    @Override
    public Notification provideNotification() {

        if(!this.settingsManager.getShowEndOfDayNotification())
            return null;

        Date lastTimePublished = this.notificationHistory.getLastTimeNotificationWasPublished(NOTIFICATION_ID_STRING);
        if(lastTimePublished != null
                && DateTimeHelper.isTodayDate(lastTimePublished))
        {
            return null;
        }
        
            Intent resultIntent = this.context.createIntent(MonthlyGoalsActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
        Notification result = this.context.buildNotification(
                R.drawable.ic_done_white_24dp,
                this.context.getStringFromResources(R.string.notification_endOfDay_title),
                this.context.getStringFromResources(R.string.notification_endOfDay_content),
                this.context.getColorArgbFromResources(R.color.planningStateButton_stateSuccess_backgroundColor),
                this.context.createPendingIntent(0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        return result;
    }

    @Override
    public void scheduleNotification() {
        HourMinuteTime notificationTime = this.settingsManager.getEndOfDayNotificationTime();
        Calendar inTime = DateTimeHelper.getTodayCalendar(notificationTime.getHour(), notificationTime.getMinute(), 0);

        this.logger.v(LOG_TAG, String.format("Scheduled EOD notification in time: %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", inTime));

        Intent notificationIntent = this.context.createIntent(NotificationPublisher.class);
        notificationIntent.putExtra(NotificationScheduler.NOTIFICATION_PROVIDER_NAME, this.getClass().getName());
        PendingIntent pendingIntent = this.context.getBroadcast(EndOfDayNotificationProvider.NOTIFICATION_ID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, inTime.getTimeInMillis(), pendingIntent);
    }
}
