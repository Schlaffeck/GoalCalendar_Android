package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.service.NotificationScheduler;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class PlannedForTodayNotificationProvider implements NotificationProvider {

    static final int NOTIFICATION_PLANNED_FOR_TODAY_ID = 1;

    private final Context context;
    private final PersistenceContext persistenceContext;

    public PlannedForTodayNotificationProvider(Context context, PersistenceContext persistenceContext)
    {
        this.context = context;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_PLANNED_FOR_TODAY_ID;
    }

    @Override
    public Notification provideNotification() {
        return this.createNotificationForTasksPlannedForToday();
    }

    private Notification createNotificationForTasksPlannedForToday()
    {
        Notification result = null;
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();

        try {
            long countCategoriesPlannedForToday = this.countCategoriesPlannedForToday(uow);

            if(countCategoriesPlannedForToday > 0)
            {
                Intent resultIntent = new Intent(this.context, MonthlyGoalsActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
                result = this.buildNotification(resultIntent, countCategoriesPlannedForToday);
            }
        }
        finally {
            uow.complete();
        }

        return result;
    }

    private Notification buildNotification(Intent resultIntent, long countCategoriesPlannedForToday)
    {
        NotificationCompat.Builder notificationBuilder
                = new NotificationCompat.Builder(this.context)
                .setSmallIcon(R.drawable.ic_date_range_white_24dp)
                .setContentTitle(this.context.getString(
                        countCategoriesPlannedForToday > 1 ?
                            R.string.notification_plannedForToday_title :
                            R.string.notification_plannedForToday_single_content))
                .setContentText(
                        String.format(
                                this.context.getString(R.string.notification_plannedForToday_content),
                                countCategoriesPlannedForToday
                        ))
                .setContentIntent(
                        PendingIntent.getActivity(this.context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                );

        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setColor(ContextCompat.getColor(this.context, R.color.planningStateButton_statePlanned_foregroundColor));

        return notificationBuilder.build();
    }

    private long countCategoriesPlannedForToday(UnitOfWork uow) {

        long countPlannedForToday = uow.getCategoryRepository()
                .findForDateWithStatus(
                        DateTimeHelper.getCurrentYear(),
                        Month.getCurrentMonth(),
                        DateTimeHelper.currentDayNumber(),
                        PlanStatus.Planned)
                .size();

        return countPlannedForToday;
    }
}
