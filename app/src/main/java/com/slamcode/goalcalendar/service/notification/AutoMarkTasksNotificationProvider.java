package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;

/**
 * Created by moriasla on 10.02.2017.
 */

public final class AutoMarkTasksNotificationProvider implements NotificationProvider {

    private static final int NOTIFICATION_ID = 3;

    @Override
    public int getNotificationId() {
        return 3;
    }

    @Override
    public Notification provideNotification() {
        return null;
    }
}
