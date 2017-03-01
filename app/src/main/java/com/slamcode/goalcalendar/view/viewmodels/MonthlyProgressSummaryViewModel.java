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
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.DefaultComparator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Transformer;

/**
 * Created by moriasla on 24.02.2017.
 */

public class MonthlyProgressSummaryViewModel extends BaseObservable {

    private final PlansSummaryCalculator calculator;
    private final MonthlyPlansModel model;

    private DailyPlansPropertyObserver dailyPlanPropertyObserver = new DailyPlansPropertyObserver();

    private ObservableArrayList<CategoryPlansSummaryViewModel> categorySummaryList;

    private PlansSummaryCalculator.MonthPlansSummary monthPlansSummary;
    private boolean statusChanged;

    public MonthlyProgressSummaryViewModel(PlansSummaryCalculator calculator, MonthlyPlansModel model)
    {
        this.calculator = calculator;
        this.model = model;
        this.setupPlansListeners();
    }

    @Bindable
    public double getPlansSummaryPercentage()
    {
        if(this.monthPlansSummary == null || statusChanged)
            this.monthPlansSummary = this.calculator.calculatePlansSummaryForMonth(model.getYear(), model.getMonth());

        statusChanged = false;
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
    }

    @Bindable
    public ObservableList<CategoryPlansSummaryViewModel> getCategorySummaryList()
    {
        return this.categorySummaryList;
    }

    public CategoryPlansRecyclerViewAdapter getCategoryPlansSummaryListAdapter(Context context, LayoutInflater layoutInflater)
    {
        SortedList<CategoryPlansSummaryViewModel> list =
                new SortedList<>(CategoryPlansSummaryViewModel.class, new ComparatorSortedListCallback<>(new DefaultComparator<CategoryPlansSummaryViewModel>()));
        list.addAll(this.getCategorySummaryList());
        return new CategoryPlansRecyclerViewAdapter(context, layoutInflater,
               list);
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
        this.categorySummaryList.addAll(CollectionUtils.collect(this.monthPlansSummary.getCompositeSummaries(), new Transformer<PlansSummaryCalculator.PlansSummary, CategoryPlansSummaryViewModel>() {
            @Override
            public CategoryPlansSummaryViewModel transform(PlansSummaryCalculator.PlansSummary input) {
                if(input instanceof PlansSummaryCalculator.CategoryPlansSummary)
                    return new CategoryPlansSummaryViewModel((PlansSummaryCalculator.CategoryPlansSummary)input);

                return null;
            }
        }));
    }

    private class DailyPlansPropertyObserver extends OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if(i == BR.status) {
                statusChanged = true;
                notifyPropertyChanged(BR.plansSummaryPercentage);
            }
        }
    }
}
