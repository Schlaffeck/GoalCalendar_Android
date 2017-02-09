package com.slamcode.goalcalendar;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import static android.R.attr.id;

/**
 * Created by moriasla on 07.02.2017.
 */

public final class DefaultApplicationContext implements ApplicationContext {

    private final Context context;

    public DefaultApplicationContext(Context context)
    {
        this.context = context;
    }

    @Override
    public Context getDefaultContext() {
        return this.context;
    }

    @Override
    public String getStringFromResources(int resourceId) {
        return this.context.getString(resourceId);
    }

    @Override
    public int getColorArgbFromResources(int colorId) {
        return ContextCompat.getColor(this.context, colorId);
    }

    @Override
    public Intent createIntent(Class<? extends Activity> activityClass) {
        return new Intent(this.context, activityClass);
    }

    @Override
    public PendingIntent createPendingIntent(int requestCode, Intent resultIntent, int flag) {
        return PendingIntent.getActivity(this.context, requestCode, resultIntent, flag);
    }

    @Override
    public Notification buildNotification(int smallIconId, String title, String content, int colorArgb, PendingIntent pendingIntent) {
        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new android.support.v4.app.NotificationCompat.Builder(this.context);
        notificationBuilder.setSmallIcon(smallIconId);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(content);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setColor(colorArgb);

        return notificationBuilder.build();
    }
}