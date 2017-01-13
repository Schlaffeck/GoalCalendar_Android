package com.slamcode.goalcalendar.dagger2;

import android.app.Application;

import com.slamcode.goalcalendar.data.dagger2.*;

/**
 * Created by moriasla on 04.01.2017.
 */

public final class ComposableApplication extends Application {

    private ApplicationDagger2Component applicationComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.applicationComponent = DaggerApplicationDagger2Component.builder()
                .dataDagger2Module(new DataDagger2Module(this))
                .build();
    }

    public ApplicationDagger2Component getApplicationComponent()
    {
        return this.applicationComponent;
    }
}
