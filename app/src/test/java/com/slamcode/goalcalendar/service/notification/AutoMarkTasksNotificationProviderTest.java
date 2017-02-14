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
import com.slamcode.goalcalendar.service.AutoMarkTasksService;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by moriasla on 13.02.2017.
 */
public class AutoMarkTasksNotificationProviderTest {

    @Test
    public void autoMarkTasksNotificationProvider_getNotificationId_test() throws Exception {
        AutoMarkTasksService serviceMock = mock(AutoMarkTasksService.class);
        ApplicationContext applicationContextMock = mock(ApplicationContext.class);

        Logger loggerMock = mock(Logger.class);

        AutoMarkTasksNotificationProvider provider = new AutoMarkTasksNotificationProvider(applicationContextMock, serviceMock, loggerMock);

        assertEquals(3, provider.getNotificationId());

        verify(serviceMock, never()).markUnfinishedTasksAsFailed();
    }

    @Test
    public void autoMarkTasksNotificationProvider_scheduleNotification_test() throws Exception {
        // create and orchestrate mocks
        AutoMarkTasksService serviceMock = mock(AutoMarkTasksService.class);
        ApplicationContext contextMock = mock(ApplicationContext.class);

        Intent intent = mock(Intent.class);
        when(contextMock.createIntent(NotificationPublisher.class)).thenReturn(intent);
        PendingIntent pendingIntent = mock(PendingIntent.class);
        when(contextMock.getBroadcast(3, intent, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntent);
        final AlarmManager alarmManager = mock(AlarmManager.class);
        when(contextMock.getSystemService(Context.ALARM_SERVICE)).thenReturn(alarmManager);

        Logger loggerMock = mock(Logger.class);

        // create provider
        AutoMarkTasksNotificationProvider provider = new AutoMarkTasksNotificationProvider(contextMock, serviceMock, loggerMock);

        // run method
        provider.scheduleNotification();

        // verify mocks
        verifyZeroInteractions(serviceMock);
        verify(intent, times(1)).putExtra(NotificationScheduler.NOTIFICATION_PROVIDER_NAME, AutoMarkTasksNotificationProvider.class.getName());
        verify(alarmManager, times(1)).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10000, pendingIntent);
        verify(loggerMock, times(1)).v("GOAL_AutoMNotPrv", "Scheduled auto mark notification in 10 secs");
    }

    @Test
    public void autoMarkTasksNotificationProvider_provideNotification_nothingMarked_test() throws Exception {
        // mock
        AutoMarkTasksService serviceMock = mock(AutoMarkTasksService.class);
        AutoMarkTasksService.AutoMarkResult result = new AutoMarkTasksService.AutoMarkResult();
        result.setWasRun(true);
        result.setUnfinishedTasksMarkedFailedCount(0);
        when(serviceMock.markUnfinishedTasksAsFailed()).thenReturn(result);

        ApplicationContext applicationContextMock = mock(ApplicationContext.class);
        Logger loggerMock = mock(Logger.class);

        // create
        AutoMarkTasksNotificationProvider provider = new AutoMarkTasksNotificationProvider(applicationContextMock, serviceMock, loggerMock);

        // run
        Notification notification = provider.provideNotification();
        assertNull(notification);

        // verify
        verify(serviceMock, times(1)).markUnfinishedTasksAsFailed();
        verifyZeroInteractions(applicationContextMock);
        verifyZeroInteractions(loggerMock);
    }


    @Test
    public void autoMarkTasksNotificationProvider_provideNotification_singleTaskMarked_test() throws Exception {
        // mock
        AutoMarkTasksService serviceMock = mock(AutoMarkTasksService.class);
        AutoMarkTasksService.AutoMarkResult result = new AutoMarkTasksService.AutoMarkResult();
        result.setWasRun(true);
        result.setUnfinishedTasksMarkedFailedCount(1);
        when(serviceMock.markUnfinishedTasksAsFailed()).thenReturn(result);

        ApplicationContext contextMock = mock(ApplicationContext.class);
        Logger loggerMock = mock(Logger.class);

        Intent intentMock = mock(Intent.class);
        when(contextMock.createIntent(MonthlyGoalsActivity.class)).thenReturn(intentMock);
        when(contextMock.getColorArgbFromResources(R.color.planningStateButton_stateFailed_foregroundColor)).thenReturn(33);
        when(contextMock.getStringFromResources(R.string.notification_autoMarked_title)).thenReturn("Title");
        when(contextMock.getStringFromResources(R.string.notification_autoMarked_single_content)).thenReturn("Content");

        PendingIntent pendingIntent = mock(PendingIntent.class);
        when(contextMock.createPendingIntent(3, intentMock, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntent);

        Notification notificationMock = mock(Notification.class);
        when(contextMock.buildNotification(R.drawable.ic_clear_white_24dp, "Title", "Content", 33, pendingIntent)).thenReturn(notificationMock);

        // create
        AutoMarkTasksNotificationProvider provider = new AutoMarkTasksNotificationProvider(contextMock, serviceMock, loggerMock);

        // run
        Notification notification = provider.provideNotification();
        assertEquals(notificationMock, notification);

        // verify
        verify(serviceMock, times(1)).markUnfinishedTasksAsFailed();
        verify(contextMock, times(1)).getStringFromResources(R.string.notification_autoMarked_title);
        verify(contextMock, times(1)).getStringFromResources(R.string.notification_autoMarked_single_content);
        verify(contextMock, times(1)).getColorArgbFromResources(R.color.planningStateButton_stateFailed_foregroundColor);
        verify(contextMock, times(1)).createIntent(MonthlyGoalsActivity.class);
        verify(contextMock, times(1)).createPendingIntent(3, intentMock, PendingIntent.FLAG_UPDATE_CURRENT);
        verify(contextMock, times(1)).buildNotification(R.drawable.ic_clear_white_24dp, "Title", "Content", 33, pendingIntent);
        verifyZeroInteractions(loggerMock);
    }

    @Test
    public void autoMarkTasksNotificationProvider_provideNotification_multipleTasksMarked_test() throws Exception {
        // mock
        AutoMarkTasksService serviceMock = mock(AutoMarkTasksService.class);
        AutoMarkTasksService.AutoMarkResult result = new AutoMarkTasksService.AutoMarkResult();
        result.setWasRun(true);
        result.setUnfinishedTasksMarkedFailedCount(3);
        when(serviceMock.markUnfinishedTasksAsFailed()).thenReturn(result);

        ApplicationContext contextMock = mock(ApplicationContext.class);
        Logger loggerMock = mock(Logger.class);

        Intent intentMock = mock(Intent.class);
        when(contextMock.createIntent(MonthlyGoalsActivity.class)).thenReturn(intentMock);
        when(contextMock.getColorArgbFromResources(R.color.planningStateButton_stateFailed_foregroundColor)).thenReturn(33);
        when(contextMock.getStringFromResources(R.string.notification_autoMarked_title)).thenReturn("Title");
        when(contextMock.getStringFromResources(R.string.notification_autoMarked_multiple_content)).thenReturn("Content to format: %d");

        PendingIntent pendingIntent = mock(PendingIntent.class);
        when(contextMock.createPendingIntent(3, intentMock, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntent);

        Notification notificationMock = mock(Notification.class);
        when(contextMock.buildNotification(R.drawable.ic_clear_white_24dp, "Title", "Content to format: 3", 33, pendingIntent)).thenReturn(notificationMock);

        // create
        AutoMarkTasksNotificationProvider provider = new AutoMarkTasksNotificationProvider(contextMock, serviceMock, loggerMock);

        // run
        Notification notification = provider.provideNotification();
        assertEquals(notificationMock, notification);

        // verify
        verify(serviceMock, times(1)).markUnfinishedTasksAsFailed();
        verify(contextMock, times(1)).getStringFromResources(R.string.notification_autoMarked_title);
        verify(contextMock, times(1)).getStringFromResources(R.string.notification_autoMarked_multiple_content);
        verify(contextMock, times(1)).getColorArgbFromResources(R.color.planningStateButton_stateFailed_foregroundColor);
        verify(contextMock, times(1)).createIntent(MonthlyGoalsActivity.class);
        verify(contextMock, times(1)).createPendingIntent(3, intentMock, PendingIntent.FLAG_UPDATE_CURRENT);
        verify(contextMock, times(1)).buildNotification(R.drawable.ic_clear_white_24dp, "Title", "Content to format: 3", 33, pendingIntent);
        verifyZeroInteractions(loggerMock);
    }
}