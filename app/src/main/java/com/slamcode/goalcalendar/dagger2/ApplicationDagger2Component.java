package com.slamcode.goalcalendar.dagger2;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.data.dagger2.DataDagger2Module;
import com.slamcode.goalcalendar.view.dagger2.ViewDagger2Module;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by moriasla on 04.01.2017.
 */
@Singleton
@Component(modules = { DataDagger2Module.class, ViewDagger2Module.class })
public interface ApplicationDagger2Component {

    void inject(MonthlyGoalsActivity activity);
}
