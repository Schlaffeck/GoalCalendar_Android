package com.slamcode.goalcalendar.view.viewmodels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.PlansSummaryForCategoriesRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.DefaultComparator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;

import java.util.List;

/**
 * Created by moriasla on 24.02.2017.
 */

public class PlansSummaryForMonthViewModel extends BaseObservable {

    private final PlansSummaryCalculator calculator;
    private final MonthlyPlansModel model;

    private DailyPlansPropertyObserver dailyPlanPropertyObserver = new DailyPlansPropertyObserver();

    private PlansSummaryCalculator.MonthPlansSummary monthPlansSummary;
    private boolean statusChanged;
    private ObservableArrayList<PlansSummaryForCategoryViewModel> categorySummaryList;

    public PlansSummaryForMonthViewModel(PlansSummaryCalculator calculator, MonthlyPlansModel model)
    {
        this.calculator = calculator;
        this.model = model;
        this.setupPlansListeners();
    }

    @Bindable
    public double getPlansSummaryPercentage()
    {
        if(this.monthPlansSummary == null || statusChanged)
            setupPlansListeners();
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

    public void refreshData()
    {
        setupPlansListeners();
        notifyPropertyChanged(BR.progressSummaryValue);
        notifyPropertyChanged(BR.categorySummaryList);
    }

    @Bindable
    public List<PlansSummaryForCategoryViewModel> getCategorySummaryList()
    {
        if(this.monthPlansSummary == null || statusChanged)
            setupPlansListeners();

        return this.categorySummaryList;
    }

    private void setupPlansListeners() {
        if(this.model == null)
            return;

        this.monthPlansSummary = this.calculator.calculatePlansSummaryForMonth(model.getYear(), model.getMonth());
        for (CategoryModel category : model.getCategories() ){
            for (DailyPlanModel dailyPlan: category.getDailyPlans()) {
                dailyPlan.removeOnPropertyChangedCallback(this.dailyPlanPropertyObserver);
                dailyPlan.addOnPropertyChangedCallback(this.dailyPlanPropertyObserver);
            }
        }

        this.categorySummaryList = new ObservableArrayList<>();
        this.categorySummaryList.addAll(CollectionUtils.collect(this.monthPlansSummary.getCompositeSummaries(), new Transformer<PlansSummaryCalculator.PlansSummary, PlansSummaryForCategoryViewModel>() {
            @Override
            public PlansSummaryForCategoryViewModel transform(PlansSummaryCalculator.PlansSummary input) {
                if(input instanceof PlansSummaryCalculator.CategoryPlansSummary)
                    return new PlansSummaryForCategoryViewModel((PlansSummaryCalculator.CategoryPlansSummary)input);

                return null;
            }
        }));

        statusChanged = false;
    }

    private class DailyPlansPropertyObserver extends OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if(i == BR.status) {
                statusChanged = true;
                notifyPropertyChanged(BR.plansSummaryPercentage);
                notifyPropertyChanged(BR.categorySummaryList);
            }
        }
    }
}
