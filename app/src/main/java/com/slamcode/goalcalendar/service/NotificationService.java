package com.slamcode.goalcalendar.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.slamcode.goalcalendar.dagger2.ComposableService;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.service.notification.PlannedForTodayNotificationProvider;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class NotificationService extends ComposableService {

    public static final String NOTIFICATION_PROVIDER_NAME = "NotificationProviderName";
    public static final String NOTIFICATION_ORIGINATED_FROM_FLAG = "OriginatedFromNotification";

    @Inject
    PersistenceContext persistenceContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // todo: create binder implementation
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        this.scheduleNotificationForTasksPlannedForToday();
        return result;
    }

    @Override
    protected void injectDependencies() {
        this.getApplicationComponent().inject(this);
    }

    private void scheduleNotificationForTasksPlannedForToday()
    {
        Calendar in10secs = DateTimeHelper.getNowCalendar();
        in10secs.add(Calendar.SECOND, 10);
        long diffMillis = DateTimeHelper.getDiffTimeMillis(DateTimeHelper.getNowCalendar(), in10secs);

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_PROVIDER_NAME, PlannedForTodayNotificationProvider.class.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + diffMillis, pendingIntent);
    }

    public static boolean checkIfOriginatedFromNotification(Activity activity)
    {
        Intent intent = activity.getIntent();
        if(intent == null)
            return false;

        boolean result = intent.getBooleanExtra(NotificationService.NOTIFICATION_ORIGINATED_FROM_FLAG, false);
        return result;
    }
}
