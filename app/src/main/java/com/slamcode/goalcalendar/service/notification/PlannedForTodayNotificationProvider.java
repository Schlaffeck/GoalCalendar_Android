package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.service.NotificationScheduler;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class PlannedForTodayNotificationProvider implements NotificationProvider {

    public static final int NOTIFICATION_ID = 1;

    private final ApplicationContext context;
    private final PersistenceContext persistenceContext;
    private final AppSettingsManager settingsManager;

    public PlannedForTodayNotificationProvider(ApplicationContext context,
                                               PersistenceContext persistenceContext,
                                               AppSettingsManager settingsManager)
    {
        this.context = context;
        this.persistenceContext = persistenceContext;
        this.settingsManager = settingsManager;
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public Notification provideNotification() {
        if(!this.settingsManager.getShowStartupNotification())
            return null;

        Notification result = null;
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();

        try {
            long countCategoriesPlannedForToday = this.countCategoriesPlannedForToday(uow);

            if(countCategoriesPlannedForToday > 0)
            {
                Intent resultIntent = this.context.createIntent(MonthlyGoalsActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
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
                        this.context.getColorArgbFromResources(R.color.planningStateButton_statePlanned_foregroundColor),
                        this.context.createPendingIntent(0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            }
        }
        finally {
            uow.complete();
        }

        return result;
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
}
