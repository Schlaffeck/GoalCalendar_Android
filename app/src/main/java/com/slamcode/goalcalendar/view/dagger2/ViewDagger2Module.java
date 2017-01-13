package com.slamcode.goalcalendar.view.dagger2;

import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 13.01.2017.
 */
@Module
public final class ViewDagger2Module {

    @Provides
    @Singleton
    public ActivityViewStateProvider provideActivityViewStateProvider()
    {
        return new ActivityViewStateProvider();
    }
}
