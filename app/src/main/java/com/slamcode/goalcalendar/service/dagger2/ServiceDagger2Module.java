package com.slamcode.goalcalendar.service.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.service.NotificationService;
import com.slamcode.goalcalendar.service.notification.EndOfDayNotificationProvider;
import com.slamcode.goalcalendar.service.notification.NotificationProvider;
import com.slamcode.goalcalendar.service.notification.PlannedForTodayNotificationProvider;

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

    private final Context context;

    public ServiceDagger2Module(Context context)
    {
        this.context = context;
    }

    @Provides
    public Map<String, NotificationProvider> getNotificationProvidersMap(PersistenceContext persistenceContext)
    {
        HashMap<String, NotificationProvider> providerHashMap = new HashMap<>();

        providerHashMap.put(PlannedForTodayNotificationProvider.class.getName(),
                new PlannedForTodayNotificationProvider(this.context, persistenceContext));

        providerHashMap.put(EndOfDayNotificationProvider.class.getName(),
                new EndOfDayNotificationProvider(this.context));

        return providerHashMap;
    }

    @Provides
    @Singleton
    public NotificationService provideNotificationService()
    {
        return new NotificationService();
    }
}
