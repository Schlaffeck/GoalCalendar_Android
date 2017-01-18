package com.slamcode.goalcalendar.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by moriasla on 18.01.2017.
 */

public final class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
                || Intent.ACTION_SCREEN_ON.equals(intent.getAction()))
        {
                Intent serviceIntent = new Intent(context, NotificationService.class);
                context.startService(serviceIntent);
        }
    }
}
