package com.slamcode.goalcalendar.data.dagger2;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by moriasla on 04.01.2017.
 */
@Singleton
@Component(modules = {DataDagger2Module.class})
public interface DataDagger2Component {

    void inject(MonthlyGoalsActivity activity);
}
