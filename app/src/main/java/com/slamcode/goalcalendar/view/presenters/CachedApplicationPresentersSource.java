package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.ViewProcessingState;
import com.slamcode.goalcalendar.view.activity.MonthlyGoalsActivityContract;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;

/**
 * Created by moriasla on 24.02.2017.
 */

public class CachedApplicationPresentersSource implements PresentersSource {

    private MonthlyGoalsPresenter monthlyGoalsPresenter;

    private PersistenceContext persistenceContext;

    private ApplicationContext applicationContext;

    private ItemsCollectionAdapterProvider listAdapterProvider;

    private PlansSummaryCalculator plansSummaryCalculator;
    private final ViewProcessingState viewProcessingState;

    public CachedApplicationPresentersSource(ApplicationContext applicationContext,
                                             PersistenceContext persistenceContext,
                                             ItemsCollectionAdapterProvider listAdapterProvider,
                                             PlansSummaryCalculator plansSummaryCalculator,
                                             ViewProcessingState viewProcessingState)
    {
        this.applicationContext = applicationContext;
        this.persistenceContext = persistenceContext;
        this.listAdapterProvider = listAdapterProvider;
        this.plansSummaryCalculator = plansSummaryCalculator;
        this.viewProcessingState = viewProcessingState;
    }

    @Override
    public MonthlyGoalsPresenter getMonthlyGoalsPresenter(MonthlyGoalsActivityContract.ActivityView activityView) {
        if(this.monthlyGoalsPresenter == null)
        {
            this.monthlyGoalsPresenter = new PersistentMonthlyGoalsPresenter(
                    this.applicationContext,
                    this.persistenceContext,
                    this.plansSummaryCalculator,
                    this.viewProcessingState);
        }
        return this.monthlyGoalsPresenter;
    }
}
