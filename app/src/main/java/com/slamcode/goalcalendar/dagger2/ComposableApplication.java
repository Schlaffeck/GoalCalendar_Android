package com.slamcode.goalcalendar.dagger2;

import android.app.Application;

import com.slamcode.goalcalendar.data.dagger2.*;
import com.slamcode.goalcalendar.service.dagger2.ServiceDagger2Module;
import com.slamcode.goalcalendar.view.dagger2.ViewDagger2Module;

/**
 * Created by moriasla on 04.01.2017.
 */

public final class ComposableApplication extends Application {

    private ApplicationDagger2Component applicationComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.applicationComponent = Dagger2ComponentContainer.getApplicationDagger2Component(this);
    }

    public ApplicationDagger2Component getApplicationComponent()
    {
        return this.applicationComponent;
    }
}
