package com.slamcode.goalcalendar.planning.dagger2;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangeListenersRegistry;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangedService;
import com.slamcode.goalcalendar.planning.summary.DataBasedPlansSummaryCalculator;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 24.02.2017.
 */
@Module
public final class PlanningDagger2Module {

    @Provides
    @Singleton
    PlansSummaryCalculator providePlansSummaryCalculator(PersistenceContext persistenceContext)
    {
        return new DataBasedPlansSummaryCalculator(persistenceContext.createUnitOfWork().getCategoriesRepository());
    }

    @Provides
    @Singleton
    DateTimeChangeListenersRegistry provideDateTimeChangeListenersRegistry()
    {
        return new DateTimeChangeListenersRegistry();
    }

    @Provides
    @Singleton
    DateTimeChangedService provideDateTimeChangedReceiver(ApplicationContext applicationContext,
                                                          DateTimeChangeListenersRegistry listenersRegistry)
    {
        return new DateTimeChangedService(applicationContext, listenersRegistry);
    }
}
