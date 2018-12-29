package com.slamcode.goalcalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.slamcode.goalcalendar.planning.DateTime;

/**
 * general application context interface introduced toe ase mocking and testing
 * most common usage of context across project
 */

public interface ApplicationContext {

    Context getDefaultContext();

    String getStringFromResources(int resourceId);

    int getColorArgbFromResources(int colorId);

    Intent createIntent(Class<?> activityOrServiceClass);

    PendingIntent createPendingIntent(int id, Intent resultIntent, int flag);

    Notification buildNotification(int smallIconId, String title, String content, int colorArgb, PendingIntent pendingIntent);

    PendingIntent getBroadcast(int notificationId, Intent notificationIntent, int intentFlag);

    Object getSystemService(String serviceId);

    Snackbar showSnackbar(View view, String message, int durationFlag, String actionName, View.OnClickListener actionOnClickListener);

    DateTime getDateTimeNow();

    AlertDialog showConfirmDialog(Activity activity, String title, String message, DialogInterface.OnClickListener yesAction, DialogInterface.OnClickListener noAction);
}
