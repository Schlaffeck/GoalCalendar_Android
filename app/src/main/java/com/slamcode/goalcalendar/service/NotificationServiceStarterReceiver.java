package com.slamcode.goalcalendar.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

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
            Intent serviceIntent = new Intent(context, NotificationScheduler.class);
            context.startService(serviceIntent);
        }
    }
}
