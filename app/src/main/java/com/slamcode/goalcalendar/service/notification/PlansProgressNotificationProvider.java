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
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryDescriptionProvider;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by smoriak on 03/07/2017.
 */

public final class PlansProgressNotificationProvider implements NotificationProvider {

    public final static String NOTIFICATION_ID_STRING = "PlansProgress";

    private static final int NOTIFICATION_ID = 3;
    private static final String LOG_TAG = "GOAL_PlansProgrNotPrv";
    private final static int NOTIFICATION_HOUR = 16;

    private final ApplicationContext applicationContext;
    private final AppSettingsManager settingsManager;
    private final Logger logger;
    private final NotificationHistory notificationHistory;
    private final PlansSummaryDescriptionProvider descriptionProvider;

    public PlansProgressNotificationProvider(ApplicationContext applicationContext,
                                             AppSettingsManager settingsManager,
                                             NotificationHistory notificationHistory,
                                             Logger logger,
                                             PlansSummaryDescriptionProvider descriptionProvider)
    {
        this.applicationContext = applicationContext;
        this.settingsManager = settingsManager;
        this.logger = logger;
        this.notificationHistory = notificationHistory;
        this.descriptionProvider = descriptionProvider;
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

        Notification notification = null;

        if(!this.settingsManager.getShowPlansProgressNotification())
            return null;

        Date lastTimePublished = this.notificationHistory.getLastTimeNotificationWasPublished(this.getNotificationIdString());
        if(lastTimePublished != null
                && DateTimeHelper.isTodayDate(lastTimePublished)
                    || this.notificationTimePassed())
        {
            return null;
        }
        DateTime dateTime = this.applicationContext.getDateTimeNow();

        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(dateTime.getYear(), dateTime.getMonth());
        if(description != null) {
            Intent intent = this.applicationContext.createIntent(MonthlyGoalsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
            PendingIntent pendingIntent = this.applicationContext.createPendingIntent(0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int color = this.applicationContext.getColorArgbFromResources(R.color.planningStateButton_statePlanned_backgroundColor);
            notification = this.applicationContext.buildNotification(
                    R.drawable.ic_calendar_range_white_24dp, description.getTitle(), description.getDetails(), color, pendingIntent);
        }

        return notification;
    }

    @Override
    public void scheduleNotification() {
        Calendar inTime = DateTimeHelper.getTodayCalendar(NOTIFICATION_HOUR, 0, 0);

        Intent notificationIntent = this.applicationContext.createIntent(NotificationPublisher.class);
        notificationIntent.putExtra(NotificationScheduler.NOTIFICATION_PROVIDER_NAME, PlansProgressNotificationProvider.class.getName());
        PendingIntent pendingIntent = this.applicationContext.getBroadcast(PlansProgressNotificationProvider.NOTIFICATION_ID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)this.applicationContext.getSystemService(Context.ALARM_SERVICE);

        this.logger.v(LOG_TAG, String.format("Scheduled middle day plans progress notification in time: %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", inTime));

        alarmManager.set(AlarmManager.RTC_WAKEUP, inTime.getTimeInMillis(), pendingIntent);
    }

    private boolean notificationTimePassed() {
        return DateTimeHelper.getNowDateTime().getHour() > NOTIFICATION_HOUR;
    }
}
