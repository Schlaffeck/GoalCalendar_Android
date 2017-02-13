package com.slamcode.goalcalendar.service.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.diagniostics.Logger;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.HourMinuteTime;
import com.slamcode.goalcalendar.service.NotificationPublisher;
import com.slamcode.goalcalendar.service.NotificationScheduler;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by moriasla on 07.02.2017.
 */
public class EndOfDayNotificationProviderTest {

    @Test
    public void endOfDayNotificationProvider_getNotificationId_test() throws Exception {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);

        EndOfDayNotificationProvider provider = new EndOfDayNotificationProvider(contextMock,  appSettingsManagerMock, loggerMock);

        assertEquals(2, provider.getNotificationId());
    }

    @Test
    public void endOfDayNotificationProvider_scheduleNotification_test() throws Exception {

        // orchestrate mocks
        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);

        HourMinuteTime hourMinuteTime = new HourMinuteTime(20, 00);
        when(appSettingsManagerMock.getEndOfDayNotificationTime()).thenReturn(hourMinuteTime);

        Intent intent = mock(Intent.class);
        when(contextMock.createIntent(NotificationPublisher.class)).thenReturn(intent);
        PendingIntent pendingIntent = mock(PendingIntent.class);
        when(contextMock.getBroadcast(2, intent, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntent);
        final AlarmManager alarmManager = mock(AlarmManager.class);
        when(contextMock.getSystemService(Context.ALARM_SERVICE)).thenReturn(alarmManager);

        // create provider
        EndOfDayNotificationProvider provider = new EndOfDayNotificationProvider(contextMock,  appSettingsManagerMock, loggerMock);

        // run method
        provider.scheduleNotification();

        // verify mocks
        Calendar calendar = DateTimeHelper.getTodayCalendar(hourMinuteTime.getHour(), hourMinuteTime.getMinute(), 0);
        verify(intent, times(1)).putExtra(NotificationScheduler.NOTIFICATION_PROVIDER_NAME, EndOfDayNotificationProvider.class.getName());
        verify(alarmManager, times(1)).set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Test
    public void endOfDayNotificationProvider_provideNotification_doNotShow_test() throws Exception {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);

        when(appSettingsManagerMock.getShowEndOfDayNotification()).thenReturn(false);

        EndOfDayNotificationProvider provider = new EndOfDayNotificationProvider(contextMock, appSettingsManagerMock, loggerMock);

        assertNull(provider.provideNotification());

        // verify method calls
        verify(appSettingsManagerMock, times(1)).getShowEndOfDayNotification();
        verifyZeroInteractions(contextMock);
    }

    @Test
    public void endOfDayNotificationProvider_provideNotification_show_test() throws Exception {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        Logger loggerMock = mock(Logger.class);

        when(appSettingsManagerMock.getShowEndOfDayNotification()).thenReturn(true);

        Intent intentMock = mock(Intent.class);
        when(contextMock.createIntent(MonthlyGoalsActivity.class)).thenReturn(intentMock);
        when(contextMock.getColorArgbFromResources(R.color.planningStateButton_stateSuccess_backgroundColor)).thenReturn(55);
        when(contextMock.getStringFromResources(R.string.notification_endOfDay_title)).thenReturn("Title");
        when(contextMock.getStringFromResources(R.string.notification_endOfDay_content)).thenReturn("Content");

        PendingIntent pendingIntentMock = mock(PendingIntent.class);
        when(contextMock.createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntentMock);

        Notification notificationMock = mock(Notification.class);
        when(contextMock.buildNotification(R.drawable.ic_done_white_24dp, "Title", "Content", 55, pendingIntentMock))
                .thenReturn(notificationMock);

        EndOfDayNotificationProvider provider = new EndOfDayNotificationProvider(contextMock, appSettingsManagerMock, loggerMock);

        assertEquals(notificationMock, provider.provideNotification());

        // verify method calls
        verify(contextMock, times(1)).createIntent(MonthlyGoalsActivity.class);
        verify(intentMock, times(1)).putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
        verify(intentMock, times(1)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        verify(contextMock, times(1)).getStringFromResources(R.string.notification_endOfDay_title);
        verify(contextMock, times(1)).getStringFromResources(R.string.notification_endOfDay_content);
        verify(contextMock, times(1)).getColorArgbFromResources(R.color.planningStateButton_stateSuccess_backgroundColor);
        verify(contextMock, times(1)).createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT);
        verify(contextMock, times(1)).buildNotification(R.drawable.ic_done_white_24dp, "Title", "Content", 55, pendingIntentMock);
    }

}