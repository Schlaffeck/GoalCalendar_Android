package com.slamcode.goalcalendar.service.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "GOAL_NotifServStarter";

    private static HashSet<String> supportedEvents;

    public static IntentFilter intentFilter;

    static
    {
        supportedEvents = new HashSet<>();
        supportedEvents.add(Intent.ACTION_BOOT_COMPLETED);
        supportedEvents.add(Intent.ACTION_SCREEN_ON);
        supportedEvents.add(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);

        intentFilter = new IntentFilter();
        for(String action : supportedEvents)
        {
            intentFilter.addAction(action);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(supportedEvents.contains(intent.getAction()))
        {
            Toast.makeText(context, String.format("Received '%s' broadcast", intent.getAction()), Toast.LENGTH_SHORT)
                .show();
            Log.v(LOG_TAG, "Starting notification scheduler");
            Intent serviceIntent = new Intent(context, NotificationScheduler.class);
            context.startService(serviceIntent);
        }
    }
}
