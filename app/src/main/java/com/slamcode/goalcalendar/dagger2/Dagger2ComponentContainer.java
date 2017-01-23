package com.slamcode.goalcalendar.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.data.dagger2.DataDagger2Module;
import com.slamcode.goalcalendar.service.dagger2.ServiceDagger2Module;
import com.slamcode.goalcalendar.settings.dagger2.SettingsDagger2Module;
import com.slamcode.goalcalendar.view.dagger2.ViewDagger2Module;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Singleton provider for injection container components
 */
public final class Dagger2ComponentContainer {

    private static ConcurrentMap<Context, ApplicationDagger2Component> defaultContextComponent = new ConcurrentHashMap<>();

    public static ApplicationDagger2Component getApplicationDagger2Component(Context context)
    {
       defaultContextComponent.putIfAbsent(context, createApplicationDagger2Component(context));
        return defaultContextComponent.get(context);
    }

    private static ApplicationDagger2Component createApplicationDagger2Component(Context context)
    {
        ApplicationDagger2Component result =  DaggerApplicationDagger2Component.builder()
                .dataDagger2Module(new DataDagger2Module(context))
                .viewDagger2Module(new ViewDagger2Module())
                .serviceDagger2Module(new ServiceDagger2Module(context))
                .settingsDagger2Module(new SettingsDagger2Module(context))
                .build();

        return result;
    }
}
