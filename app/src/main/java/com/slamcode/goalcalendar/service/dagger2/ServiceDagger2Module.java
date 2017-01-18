package com.slamcode.goalcalendar.service.dagger2;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.json.JsonFilePersistenceContext;
import com.slamcode.goalcalendar.service.NotificationService;
import com.slamcode.goalcalendar.service.StartupService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 18.01.2017.
 */
@Module
public class ServiceDagger2Module {

    @Provides
    @Singleton
    public NotificationService provideNotificationService()
    {
        return new NotificationService();
    }

    @Provides
    @Singleton
    public List<StartupService> provideStartupServices()
    {
        List<StartupService> startupServices = new ArrayList<>();
        startupServices.add(this.provideNotificationService());
        return startupServices;
    }
}
