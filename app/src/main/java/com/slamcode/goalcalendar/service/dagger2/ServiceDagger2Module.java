package com.slamcode.goalcalendar.service.dagger2;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.diagniostics.Logger;
import com.slamcode.goalcalendar.service.commands.AutoMarkTasksCommand;
import com.slamcode.goalcalendar.service.commands.SnackbarShowUpAutoMarkTasksCommand;
import com.slamcode.goalcalendar.service.notification.NotificationHistory;
import com.slamcode.goalcalendar.service.notification.NotificationScheduler;
import com.slamcode.goalcalendar.service.notification.EndOfDayNotificationProvider;
import com.slamcode.goalcalendar.service.notification.NotificationProvider;
import com.slamcode.goalcalendar.service.notification.PlannedForTodayNotificationProvider;
import com.slamcode.goalcalendar.service.notification.SharedPreferencesNotificationHistory;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 18.01.2017.
 */
@Module
public class ServiceDagger2Module {

    @Provides
    public Map<String, NotificationProvider> getNotificationProvidersMap(
            ApplicationContext applicationContext,
            PersistenceContext persistenceContext,
            AppSettingsManager settingsManager,
            NotificationHistory notificationHistory,
            Logger logger)
    {

        // todo: consider putting all providers dependencies into injectable proeprties rather tha constructors
        HashMap<String, NotificationProvider> providerHashMap = new HashMap<>();

        providerHashMap.put(PlannedForTodayNotificationProvider.class.getName(),
                new PlannedForTodayNotificationProvider(applicationContext, persistenceContext,
                        settingsManager, notificationHistory, logger));

        providerHashMap.put(EndOfDayNotificationProvider.class.getName(),
                new EndOfDayNotificationProvider(applicationContext, settingsManager, notificationHistory, logger));

        return providerHashMap;
    }

    @Provides
    @Singleton
    public NotificationScheduler provideNotificationScheduler()
    {
        return new NotificationScheduler();
    }

    @Provides
    @Singleton
    public AutoMarkTasksCommand provideAutoMarkTasksCommand(ApplicationContext applicationContext,
                                                            PersistenceContext persistenceContext,
                                                            AppSettingsManager settingsManager)
    {
        return new SnackbarShowUpAutoMarkTasksCommand(applicationContext, persistenceContext, settingsManager);
    }


    @Provides
    @Singleton
    public NotificationHistory provideNotificationHistory(ApplicationContext applicationContext,
                                                          Logger logger)
    {
        return new SharedPreferencesNotificationHistory(applicationContext, logger);
    }
}
