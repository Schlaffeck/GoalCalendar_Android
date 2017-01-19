package com.slamcode.goalcalendar.dagger2;

import android.app.Service;

import com.slamcode.goalcalendar.data.dagger2.DataDagger2Module;
import com.slamcode.goalcalendar.service.dagger2.ServiceDagger2Module;
import com.slamcode.goalcalendar.view.dagger2.ViewDagger2Module;

/**
 * Created by moriasla on 18.01.2017.
 */

public abstract class ComposableService extends Service {

    private ApplicationDagger2Component applicationComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.applicationComponent = Dagger2ComponentContainer.getApplicationDagger2Component(this);
        this.injectDependencies();
    }

    protected abstract void injectDependencies();

    public ApplicationDagger2Component getApplicationComponent()
    {
        return this.applicationComponent;
    }
}
