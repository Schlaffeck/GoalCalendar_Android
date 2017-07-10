package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.diagniostics.Logger;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryDescriptionProvider;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

/**
 * Created by smoriak on 03/07/2017.
 */

public class PlansProgressNotificationProvider implements NotificationProvider {

    public final static String NOTIFICATION_ID_STRING = "PlansProgress";

    private static final int NOTIFICATION_ID = 3;
    private static final String LOG_TAG = "GOAL_PlansProgrNotPrv";

    private final ApplicationContext applicationContext;
    private final AppSettingsManager settingsManager;
    private final Logger logger;
    private final NotificationHistory notificationHistory;
    private final PlansSummaryDescriptionProvider descriptionProvider;

    public PlansProgressNotificationProvider(ApplicationContext applicationContext,
                                             AppSettingsManager settingsManager,
                                             NotificationHistory notificationHistory,
                                             Logger logger,
                                             PlansSummaryDescriptionProvider descriptionProvider)
    {
        this.applicationContext = applicationContext;
        this.settingsManager = settingsManager;
        this.logger = logger;
        this.notificationHistory = notificationHistory;
        this.descriptionProvider = descriptionProvider;
    }

    @Override
    public int getNotificationId() {
        return NOTIFICATION_ID;
    }

    @Override
    public String getNotificationIdString() {
        return NOTIFICATION_ID_STRING;
    }

    @Override
    public Notification provideNotification() {

        Notification notification = null;

        if(!this.settingsManager.getShowPlansProgressNotification())
            return null;

        DateTime dateTime = this.applicationContext.getDateTimeNow();

        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(dateTime.getYear(), dateTime.getMonth());
        if(description != null) {
            Intent intent = this.applicationContext.createIntent(MonthlyGoalsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
            PendingIntent pendingIntent = this.applicationContext.createPendingIntent(0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int color = this.applicationContext.getColorArgbFromResources(R.color.planningStateButton_statePlanned_backgroundColor);
            notification = this.applicationContext.buildNotification(
                    R.drawable.ic_calendar_range_white_24dp, description.getTitle(), description.getDetails(), color, pendingIntent);
        }

        return notification;
    }

    @Override
    public void scheduleNotification() {

    }
}
