package com.slamcode.goalcalendar.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "GOAL_NotifServStarter";

    private HashSet<String> supportedEvents;


    public NotificationServiceStarterReceiver()
    {
        this.supportedEvents = new HashSet<>();
        this.supportedEvents.add(Intent.ACTION_BOOT_COMPLETED);
        this.supportedEvents.add(Intent.ACTION_SCREEN_ON);
        this.supportedEvents.add(Intent.ACTION_USER_PRESENT);
        this.supportedEvents.add(Intent.ACTION_LOCKED_BOOT_COMPLETED);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(this.supportedEvents.contains(intent.getAction()))
        {
            Log.v(LOG_TAG, "Starting notification scheduler");
            Intent serviceIntent = new Intent(context, NotificationScheduler.class);
            context.startService(serviceIntent);
        }
    }
}
