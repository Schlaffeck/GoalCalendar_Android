package com.slamcode.goalcalendar.view.dagger2;

import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.lists.ListAdapterProvider;
import com.slamcode.goalcalendar.view.lists.SimpleListViewAdapterProvider;
import com.slamcode.goalcalendar.view.presenters.CachedApplicationPresentersSource;
import com.slamcode.goalcalendar.view.presenters.PresentersSource;

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

    @Provides
    @Singleton
    public ListAdapterProvider provideListViewAdapterProvider()
    {
        SimpleListViewAdapterProvider provider = new SimpleListViewAdapterProvider();
        return provider;
    }

    @Provides
    @Singleton
    public PresentersSource providePresentersSource(
            PersistenceContext persistenceContext,
            ListAdapterProvider listAdapterProvider,
            PlansSummaryCalculator plansSummaryCalculator)
    {
        return new CachedApplicationPresentersSource(persistenceContext, listAdapterProvider, plansSummaryCalculator);
    }
}
