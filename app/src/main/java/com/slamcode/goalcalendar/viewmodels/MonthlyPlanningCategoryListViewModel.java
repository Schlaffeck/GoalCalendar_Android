package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;

/**
 * View model for list of categories in month
 */

public class MonthlyPlanningCategoryListViewModel extends BaseObservable {

    private final MonthlyPlansModel model;
    private final MonthViewModel monthViewModel;
    private final PlansSummaryCalculator plansSummaryCalculator;
    private ObservableList<CategoryPlansViewModel> categoryPlansList;
    private PlansSummaryCalculator.MonthPlansSummary monthPlansSummary;

    public MonthlyPlanningCategoryListViewModel(MonthlyPlansModel model, PlansSummaryCalculator plansSummaryCalculator)
    {
        this.model = model;
        this.monthViewModel = new MonthViewModel(model.getYear(), model.getMonth());
        this.plansSummaryCalculator = plansSummaryCalculator;
        this.initializeCategoryPlansList();
        this.countPlansSummary(false);
    }

    @Bindable
    public MonthViewModel getMonthData()
    {
        return this.monthViewModel;
    }

    @Bindable
    public ObservableList<CategoryPlansViewModel> getCategoryPlansList() {
        if(this.categoryPlansList == null)
            this.initializeCategoryPlansList();
        return this.categoryPlansList;
    }

    @Bindable
    public double getPlansSummaryPercentage()
    {
        return this.monthPlansSummary.getProgressPercentage();
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

    private void countPlansSummary(boolean notify)
    {
        this.monthPlansSummary = this.plansSummaryCalculator.calculatePlansSummaryForMonth(this.monthViewModel.getYear(), this.monthViewModel.getMonth());
        if(notify)
        {
            this.notifyPropertyChanged(BR.plansSummaryPercentage);
        }
    }

    private void initializeCategoryPlansList() {
        this.categoryPlansList = new ObservableArrayList<>();
        for (CategoryModel category : this.model.getCategories()) {
            this.categoryPlansList.add(this.createCategoryPlans(category));
        }
    }

    private CategoryPlansViewModel createCategoryPlans(CategoryModel category)
    {
        CategoryPlansViewModel vm = new CategoryPlansViewModel(this.monthViewModel, category, this.plansSummaryCalculator);
        vm.addOnPropertyChangedCallback(new CategoryChangeListener());
        return vm;
    }

    private class CategoryChangeListener extends OnPropertyChangedCallback{

        @Override
        public void onPropertyChanged(Observable observable, int propertyId) {
            if(propertyId == BR.progressPercentage)
                countPlansSummary(true);
        }
    }
}
