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
import com.slamcode.goalcalendar.service.AutoMarkTasksService;

/**
 * Created by moriasla on 10.02.2017.
 */

public final class AutoMarkTasksNotificationProvider implements NotificationProvider {

    private static final int NOTIFICATION_ID = 3;
    private static final String LOG_TAG = "GOAL_AutoMNotPrv";

    private final ApplicationContext context;
    private final AutoMarkTasksService service;
    private final Logger logger;

    public AutoMarkTasksNotificationProvider(ApplicationContext applicationContext, AutoMarkTasksService service, Logger logger)
    {
        this.context = applicationContext;
        this.service = service;
        this.logger = logger;
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public Notification provideNotification() {

        AutoMarkTasksService.AutoMarkResult autoMarkResult = this.service.markUnfinishedTasksAsFailed();

        Notification notification = null;

        if(autoMarkResult.getWasRun() && autoMarkResult.getUnfinishedTasksMarkedFailedCount() > 0)
        {
            String title = this.context.getStringFromResources(R.string.notification_autoMarked_title);
            String content =
                    autoMarkResult.getUnfinishedTasksMarkedFailedCount() == 1 ?
                    this.context.getStringFromResources(R.string.notification_autoMarked_single_content) :
                            String.format(this.context.getStringFromResources(R.string.notification_autoMarked_multiple_content),
                                    autoMarkResult.getUnfinishedTasksMarkedFailedCount());

            Intent intent = this.context.createIntent(MonthlyGoalsActivity.class);
            notification = this.context.buildNotification(R.drawable.ic_clear_white_24dp,
                    title, content,
                    this.context.getColorArgbFromResources(R.color.planningStateButton_stateFailed_foregroundColor),
                    this.context.createPendingIntent(NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        return notification;
    }

    @Override
    public void scheduleNotification() {


        Intent notificationIntent = this.context.createIntent(NotificationPublisher.class);
        notificationIntent.putExtra(NotificationScheduler.NOTIFICATION_PROVIDER_NAME, this.getClass().getName());
        PendingIntent pendingIntent = this.context.getBroadcast(AutoMarkTasksNotificationProvider.NOTIFICATION_ID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10000, pendingIntent);

        this.logger.v(LOG_TAG, "Scheduled auto mark notification in 10 secs");
    }
}
