package com.slamcode.goalcalendar.view.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
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

    @Bindable
    public int getNoOfDaysLeft()
    {
        int noOfDaysTotal = this.model.getMonth().getDaysCount();
        int currentYear = DateTimeHelper.getCurrentYear();
        if(this.model.getYear() > currentYear)
            return noOfDaysTotal;
        else if(this.model.getYear() < currentYear)
            return 0;

        Month currentMonth = DateTimeHelper.getCurrentMonth();
        if(this.model.getMonth().getNumValue() < currentMonth.getNumValue())
            return 0;
        else if(this.model.getMonth().getNumValue() > currentMonth.getNumValue())
            return noOfDaysTotal;

        return noOfDaysTotal - DateTimeHelper.currentDayNumber() + 1;
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
            if(i == BR.status)
                notifyPropertyChanged(BR.plansSummaryPercentage);
        }
    }
}
