package com.slamcode.goalcalendar.service.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;
import com.slamcode.goalcalendar.diagniostics.Logger;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class PlannedForTodayNotificationProvider implements NotificationProvider {

    public final static String NOTIFICATION_ID_STRING = "PlannedForToday";

    private static final int NOTIFICATION_ID = 1;
    private static final String LOG_TAG = "GOAL_PlannedNotPrv";

    private final static int NOTIFICATION_HOUR = 8;

    private final ApplicationContext context;
    private final PersistenceContext persistenceContext;
    private final AppSettingsManager settingsManager;
    private final NotificationHistory notificationHistory;
    private final Logger logger;

    public PlannedForTodayNotificationProvider(ApplicationContext context,
                                               PersistenceContext persistenceContext,
                                               AppSettingsManager settingsManager,
                                               NotificationHistory notificationHistory,
                                               Logger logger)
    {
        this.context = context;
        this.persistenceContext = persistenceContext;
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
        if(!this.settingsManager.getShowStartupNotification())
            return null;

        Date lastTimePublished = this.notificationHistory.getLastTimeNotificationWasPublished(this.getNotificationIdString());
        if(lastTimePublished != null
                && DateTimeHelper.isTodayDate(lastTimePublished)
                    || this.notificationTimePassed())
        {
            return null;
        }

        Notification result = null;
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();

        try {
            long countCategoriesPlannedForToday = this.countCategoriesPlannedForToday(uow);

            Intent resultIntent = this.context.createIntent(MonthlyGoalsActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);

            if(countCategoriesPlannedForToday > 0)
            {
                result = this.context.buildNotification(R.drawable.ic_date_range_white_24dp,
                        this.context.getStringFromResources(
                                R.string.notification_plannedForToday_title),
                        String.format(
                                this.context.getStringFromResources(
                                        countCategoriesPlannedForToday > 1 ?
                                                R.string.notification_plannedForToday_content :
                                                R.string.notification_plannedForToday_single_content),
                                countCategoriesPlannedForToday
                        ),
                        this.context.getColorArgbFromResources(R.color.flat_planningStateButton_statePlanned_foregroundColor),
                        this.context.createPendingIntent(0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            }
            else{
                result = this.context.buildNotification(R.drawable.ic_date_range_white_24dp,
                        this.context.getStringFromResources(
                                R.string.notification_plannedForToday_noPlans_title),
                        this.context.getStringFromResources(R.string.notification_plannedForToday_noPlans_content),
                        this.context.getColorArgbFromResources(R.color.flat_planningStateButton_statePlanned_foregroundColor),
                        this.context.createPendingIntent(0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            }

        }
        finally {
            uow.complete(false);
        }

        return result;
    }

    @Override
    public void scheduleNotification() {
        Calendar inTime = DateTimeHelper.getTodayCalendar(NOTIFICATION_HOUR, 0, 0);

        Intent notificationIntent = this.context.createIntent(NotificationPublisher.class);
        notificationIntent.putExtra(NotificationScheduler.NOTIFICATION_PROVIDER_NAME, PlannedForTodayNotificationProvider.class.getName());
        PendingIntent pendingIntent = this.context.getBroadcast(PlannedForTodayNotificationProvider.NOTIFICATION_ID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);

        this.logger.v(LOG_TAG, String.format("Scheduled Start of day notification in time: %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", inTime));

        alarmManager.set(AlarmManager.RTC_WAKEUP, inTime.getTimeInMillis(), pendingIntent);
    }

    private long countCategoriesPlannedForToday(UnitOfWork uow) {

        long countPlannedForToday = uow.getCategoriesRepository()
                .findForDateWithStatus(
                        DateTimeHelper.getCurrentYear(),
                        Month.getCurrentMonth(),
                        DateTimeHelper.currentDayNumber(),
                        PlanStatus.Planned)
                .size();

        return countPlannedForToday;
    }

    private boolean notificationTimePassed() {
        return DateTimeHelper.getNowDateTime().getHour() > NOTIFICATION_HOUR;
    }
}
