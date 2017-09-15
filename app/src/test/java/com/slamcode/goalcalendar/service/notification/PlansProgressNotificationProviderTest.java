package com.slamcode.goalcalendar.service.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.diagniostics.Logger;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryDescriptionProvider;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by smoriak on 03/07/2017.
 */
public class PlansProgressNotificationProviderTest {

    @Test
    public void plannedForTodayNotificationProvider_getNotificationId_test() {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);
        NotificationHistory notificationHistoryMock = Mockito.mock(NotificationHistory.class);
        PlansSummaryDescriptionProvider descriptionProviderMock = mock(PlansSummaryDescriptionProvider.class);

        PlansProgressNotificationProvider provider = new PlansProgressNotificationProvider(contextMock, appSettingsManagerMock, notificationHistoryMock, loggerMock, descriptionProviderMock);

        assertEquals(3, provider.getNotificationId());
    }

    @Test
    public void plannedForTodayNotificationProvider_getNotificationIdString_test() {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);
        NotificationHistory notificationHistoryMock = Mockito.mock(NotificationHistory.class);
        PlansSummaryDescriptionProvider descriptionProviderMock = mock(PlansSummaryDescriptionProvider.class);

        PlansProgressNotificationProvider provider = new PlansProgressNotificationProvider(contextMock, appSettingsManagerMock, notificationHistoryMock, loggerMock, descriptionProviderMock);

        assertEquals("PlansProgress", provider.getNotificationIdString());
    }

    @Test
    public void plannedForTodayNotificationProvider_provideNotification_turnedOff_test() {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);
        NotificationHistory notificationHistoryMock = Mockito.mock(NotificationHistory.class);
        PlansSummaryDescriptionProvider descriptionProviderMock = mock(PlansSummaryDescriptionProvider.class);

        // mock actions
        when(appSettingsManagerMock.getShowPlansProgressNotification()).thenReturn(false);

        PlansProgressNotificationProvider provider = new PlansProgressNotificationProvider(contextMock,
                appSettingsManagerMock,
                notificationHistoryMock,
                loggerMock,
                descriptionProviderMock);

        Notification notification = provider.provideNotification();

        assertEquals(null, notification);

        verify(appSettingsManagerMock, times(1)).getShowPlansProgressNotification();
    }

    @Test
    public void plannedForTodayNotificationProvider_provideNotification_descriptionProvided_test() {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);
        NotificationHistory notificationHistoryMock = Mockito.mock(NotificationHistory.class);
        PlansSummaryDescriptionProvider descriptionProviderMock = mock(PlansSummaryDescriptionProvider.class);

        // mock actions
        int year = 2017;
        Month month = Month.JUNE;
        when(appSettingsManagerMock.getShowPlansProgressNotification()).thenReturn(true);
        when(contextMock.getDateTimeNow()).thenReturn(new DateTime(year, month, 30));

        PlansSummaryDescriptionProvider.PlansSummaryDescription description = new PlansSummaryDescriptionProvider.PlansSummaryDescription();
        description.setDetails("Details");
        description.setTitle("Title");
        when(descriptionProviderMock.provideDescriptionForMonth(year, month)).thenReturn(description);

        Intent intentMock = mock(Intent.class);
        when(contextMock.createIntent(MonthlyGoalsActivity.class)).thenReturn(intentMock);
        PendingIntent pendingIntentMock = mock(PendingIntent.class);
        when(contextMock.createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntentMock);

        when(contextMock.getColorArgbFromResources(R.color.flat_planningStateButton_statePlanned_backgroundColor)).thenReturn(2323);

        Notification notificationMock = mock(Notification.class);
        when(contextMock.buildNotification(R.drawable.ic_calendar_range_white_24dp, "Title", "Details", 2323, pendingIntentMock)).thenReturn(notificationMock);

        PlansProgressNotificationProvider provider = new PlansProgressNotificationProvider(contextMock,
                appSettingsManagerMock,
                notificationHistoryMock,
                loggerMock,
                descriptionProviderMock);

        Notification notification = provider.provideNotification();

        assertEquals(notificationMock, notification);

        // verify calls
        verify(appSettingsManagerMock, times(1)).getShowPlansProgressNotification();
        verify(descriptionProviderMock, times(1)).provideDescriptionForMonth(2017, Month.JUNE);
        verify(contextMock, times(1)).getDateTimeNow();
        verify(contextMock, times(1)).createIntent(MonthlyGoalsActivity.class);
        verify(contextMock, times(1)).createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT);
        verify(contextMock, times(1)).buildNotification(R.drawable.ic_calendar_range_white_24dp, "Title", "Details", 2323, pendingIntentMock);
        verify(intentMock, times(1)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        verify(contextMock, times(1)).getColorArgbFromResources(R.color.flat_planningStateButton_statePlanned_backgroundColor);
        verify(intentMock, times(1)).putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
    }


    @Test
    public void plannedForTodayNotificationProvider_provideNotification_descriptionNotProvided_test() {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);
        NotificationHistory notificationHistoryMock = Mockito.mock(NotificationHistory.class);
        PlansSummaryDescriptionProvider descriptionProviderMock = mock(PlansSummaryDescriptionProvider.class);

        // mock actions
        int year = 2017;
        Month month = Month.MAY;
        when(appSettingsManagerMock.getShowPlansProgressNotification()).thenReturn(true);
        when(contextMock.getDateTimeNow()).thenReturn(new DateTime(year, month, 30));

        when(descriptionProviderMock.provideDescriptionForMonth(year, month)).thenReturn(null);

        PlansProgressNotificationProvider provider = new PlansProgressNotificationProvider(contextMock,
                appSettingsManagerMock,
                notificationHistoryMock,
                loggerMock,
                descriptionProviderMock);

        Notification notification = provider.provideNotification();

        assertNull(notification);

        // verify calls
        verify(appSettingsManagerMock, times(1)).getShowPlansProgressNotification();
        verify(descriptionProviderMock, times(1)).provideDescriptionForMonth(2017, Month.MAY);
        verify(contextMock, times(1)).getDateTimeNow();
        verifyNoMoreInteractions(contextMock);
    }

    @Test
    public void plansProgressNotificationProvider_scheduleNotification_test() {

        // orchestrate mocks
        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);

        Intent intent = mock(Intent.class);
        when(contextMock.createIntent(NotificationPublisher.class)).thenReturn(intent);
        PendingIntent pendingIntent = mock(PendingIntent.class);
        when(contextMock.getBroadcast(3, intent, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntent);
        final AlarmManager alarmManager = mock(AlarmManager.class);
        when(contextMock.getSystemService(Context.ALARM_SERVICE)).thenReturn(alarmManager);

        PlansSummaryDescriptionProvider descriptionProvider = mock(PlansSummaryDescriptionProvider.class);
        // create provider
        NotificationHistory notificationHistoryMock = Mockito.mock(NotificationHistory.class);

        PlansProgressNotificationProvider provider = new PlansProgressNotificationProvider(contextMock, appSettingsManagerMock, notificationHistoryMock, loggerMock, descriptionProvider);
        // run method
        provider.scheduleNotification();

        // verify mocks
        java.util.Calendar calendar = DateTimeHelper.getTodayCalendar(16, 0, 0);
        verify(intent, times(1)).putExtra(NotificationScheduler.NOTIFICATION_PROVIDER_NAME, PlansProgressNotificationProvider.class.getName());
        verify(alarmManager, times(1)).set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}