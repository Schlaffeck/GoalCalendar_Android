package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.ListAdapterProvider;
import com.slamcode.goalcalendar.view.mvvm.PropertyObservableObject;
import com.slamcode.goalcalendar.view.mvvm.PropertyObserver;

/**
 * Created by moriasla on 24.02.2017.
 */

public class MonthlyProgressSummary extends PropertyObservableObject {

    public static final String SUMMARY_PERCENTAGE_PROPERTY_NAME = "plansSummaryPercentage";
    private final PlansSummaryCalculator calculator;
    private final MonthlyPlansModel model;

    private DailyPlansPropertyObserver dailyPlanPropertyObserver = new DailyPlansPropertyObserver();

    public MonthlyProgressSummary(PlansSummaryCalculator calculator, MonthlyPlansModel model)
    {
        this.calculator = calculator;
        this.model = model;
        this.setupPlansListeners();
    }

    public double getPlansSummaryPercentage()
    {
        return this.calculator.calculatePlansSummaryForMonth(model.getYear(), model.getMonth()).countProgressPercentage();
    }

    public void refreshData()
    {
        setupPlansListeners();
    }

    private void setupPlansListeners() {
        if(this.model == null)
            return;

        for (CategoryModel category : model.getCategories() ){
            for (DailyPlanModel dailyPlan: category.getDailyPlans()) {
                dailyPlan.removePropertyObserver(this.dailyPlanPropertyObserver);
                dailyPlan.addPropertyObserver(this.dailyPlanPropertyObserver);
            }
        }
    }

    private class DailyPlansPropertyObserver implements PropertyObserver
    {
        @Override
        public void onPropertyChanged(String propertyName) {
            if(propertyName.toLowerCase().equals(DailyPlanModel.STATUS_PROPERTY_NAME))
                propertyChanged(SUMMARY_PERCENTAGE_PROPERTY_NAME);
        }
    }
}
