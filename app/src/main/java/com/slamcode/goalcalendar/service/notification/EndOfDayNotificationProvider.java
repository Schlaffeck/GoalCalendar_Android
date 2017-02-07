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
import com.slamcode.goalcalendar.service.NotificationScheduler;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

/**
 * Created by moriasla on 19.01.2017.
 */

public final class EndOfDayNotificationProvider implements NotificationProvider {

    public static final int NOTIFICATION_ID = 2;

    private final ApplicationContext context;
    private final AppSettingsManager settingsManager;

    public EndOfDayNotificationProvider(ApplicationContext context,
                                        AppSettingsManager settingsManager)
    {
        this.context = context;
        this.settingsManager = settingsManager;
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public Notification provideNotification() {

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
}
