package com.slamcode.goalcalendar.view.dagger2;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.charts.data.hellocharts.HelloChartsViewDataBinder;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;
import com.slamcode.goalcalendar.view.lists.AppContextBasedViewAdapterProvider;
import com.slamcode.goalcalendar.view.lists.gestures.ItemDragCallback;
import com.slamcode.goalcalendar.view.lists.scrolling.RecyclerViewSimultaneousScrollingController;
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
    public ItemsCollectionAdapterProvider provideListViewAdapterProvider()
    {
        AppContextBasedViewAdapterProvider provider = new AppContextBasedViewAdapterProvider();
        return provider;
    }

    @Provides
    @Singleton
    public PresentersSource providePresentersSource(
            ApplicationContext applicationContext,
            PersistenceContext persistenceContext,
            ItemsCollectionAdapterProvider listAdapterProvider,
            PlansSummaryCalculator plansSummaryCalculator,
            BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry)
    {
        return new CachedApplicationPresentersSource(applicationContext, persistenceContext, listAdapterProvider, plansSummaryCalculator, backupSourceDataProvidersRegistry);
    }

    @Provides
    @Singleton
    public HelloChartsViewDataBinder provideChartViewDataTransformer(ApplicationContext applicationContext)
    {
        return new HelloChartsViewDataBinder(applicationContext);
    }

    @Provides
    @Singleton
    public ItemDragCallback getCategoryListItemDragCallback()
    {
        return new ItemDragCallback();
    }

    @Provides
    @Singleton
    public RecyclerViewSimultaneousScrollingController provideCategoryPlansScrollingController()
    {
        return new RecyclerViewSimultaneousScrollingController();
    }
}
