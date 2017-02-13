package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;
import android.os.Parcelable;

/**
 * Created by moriasla on 18.01.2017.
 */

public interface NotificationProvider{

    int getNotificationId();

    Notification provideNotification();

    void scheduleNotification();
}
