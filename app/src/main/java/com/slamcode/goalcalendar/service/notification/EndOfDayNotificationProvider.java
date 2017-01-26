package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.service.NotificationScheduler;

/**
 * Created by moriasla on 19.01.2017.
 */

public final class EndOfDayNotificationProvider implements NotificationProvider {

    public static final int NOTIFICATION_ID = 2;

    private final Context context;

    public EndOfDayNotificationProvider(Context context)
    {
        this.context = context;
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public Notification provideNotification() {

        Notification result = null;
            Intent resultIntent = new Intent(this.context, MonthlyGoalsActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            resultIntent.putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
            result = this.buildNotification(resultIntent);

        return result;
    }

    private Notification buildNotification(Intent resultIntent)
    {
        NotificationCompat.Builder notificationBuilder
                = new NotificationCompat.Builder(this.context)
                .setSmallIcon(R.drawable.ic_done_white_24dp)
                .setContentTitle(this.context.getString(R.string.notification_endOfDay_title))
                .setContentText(this.context.getString(R.string.notification_endOfDay_content))
                .setContentIntent(
                        PendingIntent.getActivity(this.context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                );

        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setColor(ContextCompat.getColor(this.context, R.color.planningStateButton_stateSuccess_backgroundColor));

        return notificationBuilder.build();
    }
}
