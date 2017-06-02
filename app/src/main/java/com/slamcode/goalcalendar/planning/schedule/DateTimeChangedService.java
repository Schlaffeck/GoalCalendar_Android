package com.slamcode.goalcalendar.planning.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.slamcode.goalcalendar.ApplicationContext;

import java.util.Calendar;

/**
 * Created by schlaffeck on 09.05.2017.
 */

public final class DateTimeChangedService extends BroadcastReceiver {

    private final ApplicationContext applicationContext;
    private final DateTimeChangeListenersRegistry listenersRegistry;

    public DateTimeChangedService(ApplicationContext applicationContext, DateTimeChangeListenersRegistry listenersRegistry)
    {
        this.applicationContext = applicationContext;
        this.listenersRegistry = listenersRegistry;

        this.initialize();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(Intent.ACTION_DATE_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED) ||
                action.equals(Intent.ACTION_TIME_CHANGED))
        {
            this.notifyDateChangedListeners();
            // reschedule next notification
            this.scheduleDayChangedNotification();
        }
    }

    private void initialize()
    {
        this.scheduleDayChangedNotification();
    }

    private void notifyDateChangedListeners() {
        for(DateTimeChangeListener listener : listenersRegistry.getListeners())
        {
            listener.onDateChanged();
        }
    }

    private void scheduleDayChangedNotification()
    {
        Intent intent = new Intent(Intent.ACTION_DATE_CHANGED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.applicationContext.getDefaultContext(),
                0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) this.applicationContext.getSystemService(Service.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }
}
