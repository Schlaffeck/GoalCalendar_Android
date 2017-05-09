package com.slamcode.goalcalendar.service.notification;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.diagniostics.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by schlaffeck on 06.05.2017.
 */

public class SharedPreferencesNotificationHistory implements NotificationHistory {

    private final static String LOG_TAG = "GC_SHPrefNotHist";

    private final static String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private final static String NOTIFICATION_PUB_DATE_SUFFIX = "_DATE";

    private final ApplicationContext applicationContext;

    private SharedPreferences sharedPreferences;
    private final Logger logger;

    public SharedPreferencesNotificationHistory(ApplicationContext applicationContext, Logger logger)
    {
        this.applicationContext = applicationContext;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext.getDefaultContext());
        this.logger = logger;
    }

    @Override
    public Date getLastTimeNotificationWasPublished(String notificationId) {
        Date result = null;
        String key = notificationId + NOTIFICATION_PUB_DATE_SUFFIX;
        if(this.sharedPreferences.contains(key))
        {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING);
            String dateRead = this.sharedPreferences.getString(key, "");
            try {
                result = format.parse(dateRead);
            } catch (ParseException e) {
                e.printStackTrace();
                logger.e(LOG_TAG,  e.getMessage());
            }
        }

        return result;
    }

    @Override
    public void markNotificationWasPublished(String notificationId) {
        logger.d(LOG_TAG,  notificationId + ": marked as published");

        Date dateNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_STRING);
        String key = notificationId + NOTIFICATION_PUB_DATE_SUFFIX;

        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        String dateFormatted = formatter.format(dateNow);
        editor.putString(key, dateFormatted);
        editor.apply();
    }
}
