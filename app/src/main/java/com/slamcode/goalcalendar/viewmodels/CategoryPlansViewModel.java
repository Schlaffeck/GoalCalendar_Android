package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;

/**
 * view model for category plans in month
 */

public class CategoryPlansViewModel extends BaseObservable {

    private final MonthViewModel monthViewModel;
    private final CategoryModel model;
    private final PlansSummaryCalculator plansSummaryCalculator;
    private ObservableList<DailyPlansViewModel> dailyPlansList = new ObservableArrayList<>();

    private PlansSummaryCalculator.CategoryPlansSummary plansSummary;

    public CategoryPlansViewModel(MonthViewModel monthViewModel, CategoryModel categoryModel, PlansSummaryCalculator plansSummaryCalculator)
    {
        this.monthViewModel = monthViewModel;
        this.model = categoryModel;
        this.plansSummaryCalculator = plansSummaryCalculator;
        this.countPlansSummary(false);
    }

    @Bindable
    public String getName()
    {
        return this.model.getName();
    }

    public void setName(String newName)
    {
        if(this.model.getName() != newName)
        {
            this.model.setName(newName);
            this.notifyPropertyChanged(BR.name);
        }
    }

    @Bindable
    public FrequencyPeriod getFrequencyPeriod()
    {
        return this.model.getPeriod();
    }

    public void set(FrequencyPeriod newFrequencyPeriod)
    {
        if(this.model.getPeriod() != newFrequencyPeriod)
        {
            this.model.setPeriod(newFrequencyPeriod);
            this.notifyPropertyChanged(BR.frequencyPeriod);
        }
    }

    @Bindable
    public int getFrequencyValue()
    {
        return this.model.getFrequencyValue();
    }

    public void setFrequencyValue(int newFrequencyValue)
    {
        if(this.model.getFrequencyValue() != newFrequencyValue) {
            this.model.setFrequencyValue(newFrequencyValue);
            this.notifyPropertyChanged(BR.frequencyValue);
        }
    }

    @Bindable
    public ObservableList<DailyPlansViewModel> getDailyPlansList()
    {
        if(this.dailyPlansList == null
                || this.dailyPlansList.isEmpty())
        {
            this.initializeDailyPlansList();
        }

        return this.dailyPlansList;
    }

    @Bindable
    public boolean isDataAvailable()
    {
        return this.plansSummary.getDataAvailable();
    }

    @Bindable
    public double getProgressPercentage()
    {
        return this.plansSummary.getProgressPercentage();
    }

    @Bindable
    public int getNoOfExpectedTasks()
    {
        return this.plansSummary.getNoOfExpectedTasks();
    }

    @Bindable
    public int getNoOfFailedTasks()
    {
        return this.plansSummary.getNoOfFailedTasks();
    }

    @Bindable
    public int getNoOfSuccessfulTasks()
    {
        return this.plansSummary.getNoOfSuccessfulTasks();
    }

    private void countPlansSummary(boolean notify)
    {
        this.plansSummary = this.plansSummaryCalculator
                .calculatePlansSummaryForMonthInCategory(this.monthViewModel.getYear(), this.monthViewModel.getMonth(), this.getName());
        if(notify) {
            notifyPropertyChanged(BR.plansSummaryPercentage);
            notifyPropertyChanged(BR.noOfDaysLeft);
            notifyPropertyChanged(BR.noOfExpectedTasks);
            notifyPropertyChanged(BR.noOfFailedTasks);
            notifyPropertyChanged(BR.noOfSuccessfulTasks);
        }
    }

    private void initializeDailyPlansList() {
        if(this.dailyPlansList == null)
            this.dailyPlansList = new ObservableArrayList<>();

        for (DailyPlanModel dailyPlan : this.model.getDailyPlans()) {
            dailyPlansList.add(initializeDailyPlanViewModel(dailyPlan));
        }
    }

    private DailyPlansViewModel initializeDailyPlanViewModel(DailyPlanModel dailyPlan)
    {
        DailyPlansViewModel vm = new DailyPlansViewModel(dailyPlan);
        vm.addOnPropertyChangedCallback(new DailyPlanChangeListener());
        return vm;
    }

    private class DailyPlanChangeListener extends OnPropertyChangedCallback {

        @Override
        public void onPropertyChanged(Observable observable, int propertyId) {
            if(propertyId == BR.status)
                countPlansSummary(true);
        }
    }
}
