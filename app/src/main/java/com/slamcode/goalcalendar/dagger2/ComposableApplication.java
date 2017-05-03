package com.slamcode.goalcalendar.dagger2;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by moriasla on 04.01.2017.
 */

public final class ComposableApplication extends MultiDexApplication {

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
