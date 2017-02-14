package com.slamcode.goalcalendar.service.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.slamcode.goalcalendar.dagger2.ComposableService;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.Map;

import javax.inject.Inject;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class NotificationScheduler extends ComposableService {

    private static final String LOG_TAG = "GOAL_NotifSched";

    public static final String NOTIFICATION_PROVIDER_NAME = "NotificationProviderName";
    public static final String NOTIFICATION_ORIGINATED_FROM_FLAG = "OriginatedFromNotification";

    @Inject
    PersistenceContext persistenceContext;

    @Inject
    AppSettingsManager settingsManager;

    @Inject
    Map<String, NotificationProvider> notificationProviderMap;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // todo: create binder implementation
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        this.scheduleNotifications();
        return result;
    }

    private void scheduleNotifications()
    {
        for(NotificationProvider provider : this.notificationProviderMap.values())
        {
            provider.scheduleNotification();
        }
    }

    @Override
    protected void injectDependencies() {
        this.getApplicationComponent().inject(this);
    }

    public static boolean checkIfOriginatedFromNotification(Activity activity)
    {
        Intent intent = activity.getIntent();
        if(intent == null)
            return false;

        boolean result = intent.getBooleanExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, false);
        return result;
    }
}
