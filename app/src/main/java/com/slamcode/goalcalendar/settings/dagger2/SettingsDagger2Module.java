package com.slamcode.goalcalendar.settings.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.settings.AppSettingsManager;
import com.slamcode.goalcalendar.settings.SharedPreferencesSettingsManager;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 23.01.2017.
 */
@Module
public class SettingsDagger2Module {
    private final Context context;

    public SettingsDagger2Module(Context context)
    {
        this.context = context;
    }

    @Provides
    @Singleton
    public AppSettingsManager provideAppSettingsManager()
    {
        return new SharedPreferencesSettingsManager(this.context);
    }
}
