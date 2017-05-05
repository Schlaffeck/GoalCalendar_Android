package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;

/**
 * View model for list of categories in month
 */

public class MonthlyPlanningCategoryListViewModel extends BaseObservable {

    private final MonthlyPlansModel model;
    private final MonthViewModel monthViewModel;
    private final PlansSummaryCalculator plansSummaryCalculator;
    private final SourceChangeRequestNotifier.SourceChangeRequestListener<CategoryPlansViewModel> categoryChangeRequestListener;
    private ObservableList<CategoryPlansViewModel> categoryPlansList;
    private PlansSummaryCalculator.MonthPlansSummary monthPlansSummary;
    private OnPropertyChangedCallback categoryPropertyChangedListener;

    public MonthlyPlanningCategoryListViewModel(MonthlyPlansModel model,
                                                PlansSummaryCalculator plansSummaryCalculator,
                                                SourceChangeRequestNotifier.SourceChangeRequestListener<CategoryPlansViewModel> categoryChangeRequestListener)
    {
        this.model = model;
        this.monthViewModel = new MonthViewModel(model.getYear(), model.getMonth());
        this.plansSummaryCalculator = plansSummaryCalculator;
        this.categoryChangeRequestListener = categoryChangeRequestListener;
        this.categoryPropertyChangedListener = new CategoryPropertyChangeListener();
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

    @Bindable
    public boolean isEmpty()
    {
        return this.categoryPlansList == null || this.categoryPlansList.isEmpty();
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

        this.categoryPlansList.addOnListChangedCallback(new CategoryListChangedListener());
        this.notifyPropertyChanged(BR.empty);
    }

    private CategoryPlansViewModel createCategoryPlans(CategoryModel category)
    {
        CategoryPlansViewModel vm = new CategoryPlansViewModel(this.monthViewModel, category, this.plansSummaryCalculator);
        vm.addOnPropertyChangedCallback(this.categoryPropertyChangedListener);
        vm.addSourceChangeRequestListener(this.categoryChangeRequestListener);
        return vm;
    }

    private class CategoryPropertyChangeListener extends OnPropertyChangedCallback{

        @Override
        public void onPropertyChanged(Observable observable, int propertyId) {
            if(propertyId == BR.progressPercentage)
                countPlansSummary(true);
        }
    }

    private class CategoryListChangedListener extends ObservableList.OnListChangedCallback<ObservableList<CategoryPlansViewModel>>{

        private CategoryPlansViewModel itemRequestedForRemoval;

        @Override
        public void onChanged(ObservableList<CategoryPlansViewModel> categoryPlansViewModels) {
            model.getCategories().clear();
            for (CategoryPlansViewModel vm : categoryPlansViewModels) {
                model.getCategories().add(vm.getModel());
            }

            countPlansSummary(false);
            notifyPropertyChanged(BR.plansSummaryPercentage);
        }

        @Override
        public void onItemRangeChanged(ObservableList<CategoryPlansViewModel> categoryPlansViewModels, int positionStart, int itemCount) {

            countPlansSummary(false);
            notifyPropertyChanged(BR.plansSummaryPercentage);
        }

        @Override
        public void onItemRangeInserted(ObservableList<CategoryPlansViewModel> categoryPlansViewModels, int positionStart, int itemCount) {
            if(itemCount == 0)
                return;

            for(int i = positionStart; i < positionStart + itemCount; i++)
            {
                CategoryPlansViewModel itemVm = categoryPlansViewModels.get(i);
                itemVm.addOnPropertyChangedCallback(categoryPropertyChangedListener);
                model.getCategories().add(itemVm.getModel());
                itemVm.updatePlansSummary();
            }

            countPlansSummary(false);
            notifyPropertyChanged(BR.empty);
            notifyPropertyChanged(BR.plansSummaryPercentage);
        }

        @Override
        public void onItemRangeMoved(ObservableList<CategoryPlansViewModel> categoryPlansViewModels, int fromPosition, int toPosition, int itemCount) {

        }

        @Override
        public void onItemRangeRemoved(ObservableList<CategoryPlansViewModel> categoryPlansViewModels, int positionStart, int itemCount) {
            if(itemCount == 0)
                return;

            for(int i = positionStart; i < positionStart + itemCount && i < model.getCategories().size(); i++)
            {
                model.getCategories().remove(i);
            }

            countPlansSummary(false);
            notifyPropertyChanged(BR.empty);
            notifyPropertyChanged(BR.plansSummaryPercentage);
        }
    }
}
