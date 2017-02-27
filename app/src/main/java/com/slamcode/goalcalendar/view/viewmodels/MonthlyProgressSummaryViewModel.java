package com.slamcode.goalcalendar.view.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;

/**
 * Created by moriasla on 24.02.2017.
 */

public class MonthlyProgressSummaryViewModel extends BaseObservable {

    private final PlansSummaryCalculator calculator;
    private final MonthlyPlansModel model;

    private DailyPlansPropertyObserver dailyPlanPropertyObserver = new DailyPlansPropertyObserver();

    public MonthlyProgressSummaryViewModel(PlansSummaryCalculator calculator, MonthlyPlansModel model)
    {
        this.calculator = calculator;
        this.model = model;
        this.setupPlansListeners();
    }

    @Bindable
    public double getPlansSummaryPercentage()
    {
        return this.calculator.calculatePlansSummaryForMonth(model.getYear(), model.getMonth()).getProgressPercentage();
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
                dailyPlan.removeOnPropertyChangedCallback(this.dailyPlanPropertyObserver);
                dailyPlan.addOnPropertyChangedCallback(this.dailyPlanPropertyObserver);
            }
        }
    }

    private class DailyPlansPropertyObserver extends OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            notifyPropertyChanged(BR.plansSummaryPercentage);
        }
    }
}
