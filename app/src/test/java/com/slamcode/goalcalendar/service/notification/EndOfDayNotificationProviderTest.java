package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.service.NotificationScheduler;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by moriasla on 07.02.2017.
 */
public class EndOfDayNotificationProviderTest {

    @Test
    public void endOfDayNotificationProvider_getNotificationId_test() throws Exception {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        EndOfDayNotificationProvider provider = new EndOfDayNotificationProvider(contextMock,  appSettingsManagerMock);

        assertEquals(2, provider.getNotificationId());
    }

    @Test
    public void endOfDayNotificationProvider_provideNotification_test() throws Exception {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);

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

        EndOfDayNotificationProvider provider = new EndOfDayNotificationProvider(contextMock, appSettingsManagerMock);

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