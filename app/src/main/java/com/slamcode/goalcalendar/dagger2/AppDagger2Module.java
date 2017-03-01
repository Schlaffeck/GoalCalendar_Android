package com.slamcode.goalcalendar.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.DefaultApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 01.03.2017.
 */
@Module
public class AppDagger2Module {

    private final Context context;

    public AppDagger2Module(Context context)
    {
        this.context = context;
    }

    @Singleton
    @Provides
    ApplicationContext provideApplicationContext()
    {
        return new DefaultApplicationContext(context);
    }
}
