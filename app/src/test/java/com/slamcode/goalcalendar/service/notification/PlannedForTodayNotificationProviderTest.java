package com.slamcode.goalcalendar.service.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.test.mock.MockContext;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.MonthlyGoalsActivity_MembersInjector;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.service.NotificationScheduler;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by moriasla on 07.02.2017.
 */
public class PlannedForTodayNotificationProviderTest {

    @Test
    public void plannedForTodayNotificationProvider_getNotificationId_test() throws Exception {

        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        PersistenceContext persistenceContextMock = Mockito.mock(PersistenceContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);
        PlannedForTodayNotificationProvider provider = new PlannedForTodayNotificationProvider(contextMock, persistenceContextMock, appSettingsManagerMock);

        assertEquals(1, provider.getNotificationId());
    }

    @Test
    public void plannedForTodayNotificationProvider_provideNotification_settingSwitchedOff_test() throws Exception {
        // mocks
        ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);
        PersistenceContext persistenceContextMock = Mockito.mock(PersistenceContext.class);
        AppSettingsManager appSettingsManagerMock = Mockito.mock(AppSettingsManager.class);

        UnitOfWork uowMock = Mockito.mock(UnitOfWork.class);

        CategoriesRepository categoriesRepositoryMock = Mockito.mock(CategoriesRepository.class);

        when(appSettingsManagerMock.getShowStartupNotification()).thenReturn(false);

        // create provider
        PlannedForTodayNotificationProvider provider = new PlannedForTodayNotificationProvider(contextMock, persistenceContextMock, appSettingsManagerMock);

        // assert no notification as no plans for today
        assertNull(provider.provideNotification());

        // verify methods called
        verify(appSettingsManagerMock, times(1)).getShowStartupNotification();

    }

    @Test
    public void plannedForTodayNotificationProvider_provideNotification_nothingPlanned_test() throws Exception {
        // mocks
        ApplicationContext contextMock = mock(ApplicationContext.class);
        PersistenceContext persistenceContextMock = mock(PersistenceContext.class);
        AppSettingsManager appSettingsManagerMock = mock(AppSettingsManager.class);

        UnitOfWork uowMock = mock(UnitOfWork.class);

        CategoriesRepository categoriesRepositoryMock = mock(CategoriesRepository.class);

        when(appSettingsManagerMock.getShowStartupNotification()).thenReturn(true);
        when(persistenceContextMock.createUnitOfWork()).thenReturn(uowMock);
        when(uowMock.getCategoriesRepository()).thenReturn(categoriesRepositoryMock);

        Intent intentMock = mock(Intent.class);
        when(contextMock.createIntent(MonthlyGoalsActivity.class)).thenReturn(intentMock);
        when(contextMock.getColorArgbFromResources(R.color.planningStateButton_statePlanned_foregroundColor)).thenReturn(44);
        when(contextMock.getStringFromResources(R.string.notification_plannedForToday_noPlans_title)).thenReturn("Title 1");
        when(contextMock.getStringFromResources(R.string.notification_plannedForToday_noPlans_content)).thenReturn("Content 1");

        PendingIntent pendingIntentMock = mock(PendingIntent.class);
        when(contextMock.createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntentMock);

        Notification notificationMock = mock(Notification.class);
        when(contextMock.buildNotification(R.drawable.ic_date_range_white_24dp, "Title 1", "Content 1", 44, pendingIntentMock))
                .thenReturn(notificationMock);

        // get date time
        java.util.Calendar today = java.util.Calendar.getInstance();
        int year = today.get(java.util.Calendar.YEAR);
        Month month = Month.getCurrentMonth();
        int day = DateTimeHelper.currentDayNumber();

        when(categoriesRepositoryMock.findForDateWithStatus(year, month, day, PlanStatus.Planned))
                .thenReturn(CollectionUtils.<CategoryModel>emptyList());

        // create provider
        PlannedForTodayNotificationProvider provider = new PlannedForTodayNotificationProvider(contextMock, persistenceContextMock, appSettingsManagerMock);

        // assert notification data
        Notification actual = provider.provideNotification();
        assertEquals(notificationMock, actual);

        // verify methods called
        verify(appSettingsManagerMock, times(1)).getShowStartupNotification();
        verify(persistenceContextMock, times(1)).createUnitOfWork();
        verify(uowMock, times(1)).getCategoriesRepository();
        verify(categoriesRepositoryMock, times(1)).findForDateWithStatus(year, month, day, PlanStatus.Planned);
        verify(uowMock, times(1)).complete(false);

        verify(contextMock, times(1)).createIntent(MonthlyGoalsActivity.class);
        verify(intentMock, times(1)).putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
        verify(intentMock, times(1)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        verify(contextMock, times(1)).getStringFromResources(R.string.notification_plannedForToday_noPlans_title);
        verify(contextMock, times(1)).getStringFromResources(R.string.notification_plannedForToday_noPlans_content);
        verify(contextMock, times(1)).getColorArgbFromResources(R.color.planningStateButton_statePlanned_foregroundColor);
        verify(contextMock, times(1)).createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT);
        verify(contextMock, times(1)).buildNotification(R.drawable.ic_date_range_white_24dp, "Title 1", "Content 1", 44, pendingIntentMock);
    }

    @Test
    public void plannedForTodayNotificationProvider_provideNotification_singleThingPlanned_test() throws Exception {
        // mocks
        ApplicationContext contextMock = mock(ApplicationContext.class);
        PersistenceContext persistenceContextMock = mock(PersistenceContext.class);
        AppSettingsManager appSettingsManagerMock = mock(AppSettingsManager.class);

        UnitOfWork uowMock = mock(UnitOfWork.class);

        CategoriesRepository categoriesRepositoryMock = mock(CategoriesRepository.class);

        when(appSettingsManagerMock.getShowStartupNotification()).thenReturn(true);
        when(persistenceContextMock.createUnitOfWork()).thenReturn(uowMock);
        when(uowMock.getCategoriesRepository()).thenReturn(categoriesRepositoryMock);

        Intent intentMock = mock(Intent.class);
        when(contextMock.createIntent(MonthlyGoalsActivity.class)).thenReturn(intentMock);
        when(contextMock.getColorArgbFromResources(R.color.planningStateButton_statePlanned_foregroundColor)).thenReturn(55);
        when(contextMock.getStringFromResources(R.string.notification_plannedForToday_title)).thenReturn("Title");
        when(contextMock.getStringFromResources(R.string.notification_plannedForToday_single_content)).thenReturn("Content");

        PendingIntent pendingIntentMock = mock(PendingIntent.class);
        when(contextMock.createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntentMock);

        Notification notificationMock = mock(Notification.class);
        when(contextMock.buildNotification(R.drawable.ic_date_range_white_24dp, "Title", "Content", 55, pendingIntentMock))
                .thenReturn(notificationMock);

        // get date time
        java.util.Calendar today = java.util.Calendar.getInstance();
        int year = today.get(java.util.Calendar.YEAR);
        Month month = Month.getCurrentMonth();
        int day = DateTimeHelper.currentDayNumber();

        when(categoriesRepositoryMock.findForDateWithStatus(year, month, day, PlanStatus.Planned))
                .thenReturn(CollectionUtils.createList(new CategoryModel(1, "C1", FrequencyPeriod.Month, 2)));

        // create provider
        PlannedForTodayNotificationProvider provider = new PlannedForTodayNotificationProvider(contextMock, persistenceContextMock, appSettingsManagerMock);

        // assert notification data
        Notification actual = provider.provideNotification();
        assertEquals(notificationMock, actual);

        // verify methods called
        verify(appSettingsManagerMock, times(1)).getShowStartupNotification();
        verify(persistenceContextMock, times(1)).createUnitOfWork();
        verify(uowMock, times(1)).getCategoriesRepository();
        verify(categoriesRepositoryMock, times(1)).findForDateWithStatus(year, month, day, PlanStatus.Planned);
        verify(uowMock, times(1)).complete(false);

        verify(contextMock, times(1)).createIntent(MonthlyGoalsActivity.class);
        verify(intentMock, times(1)).putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
        verify(intentMock, times(1)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        verify(contextMock, times(1)).getStringFromResources(R.string.notification_plannedForToday_title);
        verify(contextMock, times(1)).getStringFromResources(R.string.notification_plannedForToday_single_content);
        verify(contextMock, times(1)).getColorArgbFromResources(R.color.planningStateButton_statePlanned_foregroundColor);
        verify(contextMock, times(1)).createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT);
        verify(contextMock, times(1)).buildNotification(R.drawable.ic_date_range_white_24dp, "Title", "Content", 55, pendingIntentMock);
    }

    @Test
    public void plannedForTodayNotificationProvider_provideNotification_multipleThingPlanned_test() throws Exception {
        // mocks
        ApplicationContext contextMock = mock(ApplicationContext.class);
        PersistenceContext persistenceContextMock = mock(PersistenceContext.class);
        AppSettingsManager appSettingsManagerMock = mock(AppSettingsManager.class);

        UnitOfWork uowMock = mock(UnitOfWork.class);

        CategoriesRepository categoriesRepositoryMock = mock(CategoriesRepository.class);

        when(appSettingsManagerMock.getShowStartupNotification()).thenReturn(true);
        when(persistenceContextMock.createUnitOfWork()).thenReturn(uowMock);
        when(uowMock.getCategoriesRepository()).thenReturn(categoriesRepositoryMock);

        Intent intentMock = mock(Intent.class);
        when(contextMock.createIntent(MonthlyGoalsActivity.class)).thenReturn(intentMock);
        when(contextMock.getColorArgbFromResources(R.color.planningStateButton_statePlanned_foregroundColor)).thenReturn(66);
        when(contextMock.getStringFromResources(R.string.notification_plannedForToday_title)).thenReturn("Title 2");
        when(contextMock.getStringFromResources(R.string.notification_plannedForToday_content)).thenReturn("Content 2");

        PendingIntent pendingIntentMock = mock(PendingIntent.class);
        when(contextMock.createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT)).thenReturn(pendingIntentMock);

        Notification notificationMock = mock(Notification.class);
        when(contextMock.buildNotification(R.drawable.ic_date_range_white_24dp, "Title 2", "Content 2", 66, pendingIntentMock))
                .thenReturn(notificationMock);

        // get date time
        java.util.Calendar today = java.util.Calendar.getInstance();
        int year = today.get(java.util.Calendar.YEAR);
        Month month = Month.getCurrentMonth();
        int day = DateTimeHelper.currentDayNumber();

        when(categoriesRepositoryMock.findForDateWithStatus(year, month, day, PlanStatus.Planned))
                .thenReturn(CollectionUtils.createList(
                        new CategoryModel(1, "C1", FrequencyPeriod.Month, 2),
                        new CategoryModel(1, "C2", FrequencyPeriod.Month, 2),
                        new CategoryModel(1, "C3", FrequencyPeriod.Month, 2)));

        // create provider
        PlannedForTodayNotificationProvider provider = new PlannedForTodayNotificationProvider(contextMock, persistenceContextMock, appSettingsManagerMock);

        // assert notification data
        Notification actual = provider.provideNotification();
        assertEquals(notificationMock, actual);

        // verify methods called
        verify(appSettingsManagerMock, times(1)).getShowStartupNotification();
        verify(persistenceContextMock, times(1)).createUnitOfWork();
        verify(uowMock, times(1)).getCategoriesRepository();
        verify(categoriesRepositoryMock, times(1)).findForDateWithStatus(year, month, day, PlanStatus.Planned);
        verify(uowMock, times(1)).complete(false);

        verify(contextMock, times(1)).createIntent(MonthlyGoalsActivity.class);
        verify(intentMock, times(1)).putExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, true);
        verify(intentMock, times(1)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        verify(contextMock, times(1)).getStringFromResources(R.string.notification_plannedForToday_title);
        verify(contextMock, times(1)).getStringFromResources(R.string.notification_plannedForToday_content);
        verify(contextMock, times(1)).getColorArgbFromResources(R.color.planningStateButton_statePlanned_foregroundColor);
        verify(contextMock, times(1)).createPendingIntent(0, intentMock, PendingIntent.FLAG_UPDATE_CURRENT);
        verify(contextMock, times(1)).buildNotification(R.drawable.ic_date_range_white_24dp, "Title 2", "Content 2", 66, pendingIntentMock);
    }
}