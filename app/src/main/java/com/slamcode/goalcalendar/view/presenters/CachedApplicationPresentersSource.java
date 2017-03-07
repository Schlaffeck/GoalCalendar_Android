package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

/**
 * Created by moriasla on 24.02.2017.
 */

public class CachedApplicationPresentersSource implements PresentersSource {

    private MonthlyGoalsPresenter monthlyGoalsPresenter;

    private MonthlyGoalsViewModel monthlyGoalsViewModel;

    private PersistenceContext persistenceContext;

    private ItemsCollectionAdapterProvider listAdapterProvider;

    private PlansSummaryCalculator plansSummaryCalculator;

    public CachedApplicationPresentersSource(PersistenceContext persistenceContext,
                                             ItemsCollectionAdapterProvider listAdapterProvider, PlansSummaryCalculator plansSummaryCalculator)
    {
        this.persistenceContext = persistenceContext;
        this.listAdapterProvider = listAdapterProvider;
        this.plansSummaryCalculator = plansSummaryCalculator;
    }

    @Override
    public MonthlyGoalsPresenter getMonthlyGoalsPresenter(MonthlyGoalsActivity activity) {
        if(this.monthlyGoalsPresenter == null)
        {
            this.monthlyGoalsPresenter = new PersistentMonthlyGoalsPresenter(
                    this.persistenceContext,
                    this.plansSummaryCalculator);
        }
        return this.monthlyGoalsPresenter;
    }
}
