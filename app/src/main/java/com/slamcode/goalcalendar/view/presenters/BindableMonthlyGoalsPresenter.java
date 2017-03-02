package com.slamcode.goalcalendar.view.presenters;

import android.app.AlertDialog;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.util.Pair;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.AddEditCategoryDialog;
import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;
import com.slamcode.goalcalendar.viewmodels.MonthlyPlanningCategoryListViewModel;
import com.slamcode.goalcalendar.viewmodels.PlansSummaryForMonthViewModel;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;


/**
 * Created by moriasla on 02.03.2017.
 */

public class BindableMonthlyGoalsPresenter extends BaseObservable implements MonthlyGoalsPresenter {

    private final PersistenceContext persistenceContext;
    private final PlansSummaryCalculator plansSummaryCalculator;
    private MonthlyPlanningCategoryListViewModel monthlyPlansViewModel;

    Map<YearMonthPair, MonthlyPlanningCategoryListViewModel> viewModelMap = new HashMap<>();

    public BindableMonthlyGoalsPresenter(PersistenceContext persistenceContext, PlansSummaryCalculator plansSummaryCalculator)
    {
        this.persistenceContext = persistenceContext;
        this.plansSummaryCalculator = plansSummaryCalculator;
        this.setYearAndMonth(DateTimeHelper.getCurrentYear(), DateTimeHelper.getCurrentMonth());
    }

    @Bindable
    public MonthlyPlanningCategoryListViewModel getMonthlyPlansViewModel()
    {
        return this.monthlyPlansViewModel;
    }

    @Override
    public void setYearAndMonth(int year, Month month) {
        if(this.monthlyPlansViewModel == null
                || this.monthlyPlansViewModel.getMonthData() != null
                    && (this.monthlyPlansViewModel.getMonthData().getYear() != year || this.monthlyPlansViewModel.getMonthData().getMonth() != month))
        {
            YearMonthPair yearMonthPair = new YearMonthPair(year, month);
            if(!this.viewModelMap.containsKey(yearMonthPair))
            {
                MonthlyPlansModel model = this.getMonthlyPlans(yearMonthPair);
                this.viewModelMap.put(yearMonthPair, new MonthlyPlanningCategoryListViewModel(model, this.plansSummaryCalculator));
            }

            this.monthlyPlansViewModel = this.viewModelMap.get(yearMonthPair);
            this.notifyPropertyChanged(BR.monthlyPlansViewModel);
        }
    }

    @Override
    public CategoryNameRecyclerViewAdapter getCategoryNamesRecyclerViewAdapter() {
        return null;
    }

    @Override
    public CategoryDailyPlansRecyclerViewAdapter getCategoryDailyPlansRecyclerViewAdapter() {
        return null;
    }

    @Override
    public boolean isEmptyCategoriesList() {
        return false;
    }

    @Override
    public boolean canCopyCategoriesFromPreviousMonth() {
        return false;
    }

    @Override
    public int getSelectedYear() {
        return 0;
    }

    @Override
    public Month getSelectedMonth() {
        return null;
    }

    @Override
    public String getCategoryNameOnPosition(int categoryPosition) {
        return null;
    }

    @Override
    public void copyCategoriesFromPreviousMonth() {

    }

    @Override
    public AddEditCategoryDialog createAddEditCategoryDialog(int categoryPosition) {
        return null;
    }

    @Override
    public AlertDialog createDeleteCategoryDialog(int categoryPosition) {
        return null;
    }

    @Override
    public PlansSummaryForMonthViewModel getProgressSummaryValue() {
        return null;
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {

    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {

    }

    private MonthlyPlansModel getMonthlyPlans(YearMonthPair yearMonthPair)
    {
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();

        MonthlyPlansModel model = uow.getMonthlyPlansRepository().findForMonth(yearMonthPair.year, yearMonthPair.month);
        if(model == null)
        {
            model = new MonthlyPlansModel(yearMonthPair.hashCode(), yearMonthPair.year, yearMonthPair.month);
            uow.getMonthlyPlansRepository().add(model);
        }

        uow.complete();

        return model;
    }

    private class YearMonthPair{

        int year;

        Month month;

        YearMonthPair(int year, Month month)
        {
            this.month = month;
            this.year = year;
        }

        @Override
        public int hashCode() {
            int prime = 37;
            int result = 11;

            result = prime * result + this.year;
            result = prime * result + this.month.hashCode();

            return result;
        }
    }
}
