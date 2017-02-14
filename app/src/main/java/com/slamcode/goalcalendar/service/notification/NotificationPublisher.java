package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.slamcode.goalcalendar.dagger2.Dagger2ComponentContainer;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class NotificationPublisher extends BroadcastReceiver {

    @Inject
    Map<String, NotificationProvider> notificationProviderMap;

    @Override
    public void onReceive(Context context, Intent intent) {

        Dagger2ComponentContainer
                .getApplicationDagger2Component(context)
                .inject(this);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationProviderName = intent.getStringExtra(NotificationScheduler.NOTIFICATION_PROVIDER_NAME);

        if(notificationProviderName == null
                || !this.notificationProviderMap.containsKey(notificationProviderName))
            return;

        NotificationProvider provider = notificationProviderMap.get(notificationProviderName);
        Notification notification = provider.provideNotification();
        if(notification == null)
            return;

        notificationManager.notify(provider.getNotificationId(), notification);
    }
}
