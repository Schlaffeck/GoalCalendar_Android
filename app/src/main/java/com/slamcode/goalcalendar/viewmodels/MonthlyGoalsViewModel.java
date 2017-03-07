package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.ResourcesHelper;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by moriasla on 02.03.2017.
 */

public class MonthlyGoalsViewModel extends BaseObservable {

    private final ApplicationContext applicationContext;
    private final PersistenceContext persistenceContext;
    private final PlansSummaryCalculator plansSummaryCalculator;
    private MonthlyPlanningCategoryListViewModel monthlyPlansViewModel;

    Map<YearMonthPair, MonthlyPlanningCategoryListViewModel> viewModelMap = new HashMap<>();

    public MonthlyGoalsViewModel(ApplicationContext applicationContext, PersistenceContext persistenceContext, PlansSummaryCalculator plansSummaryCalculator)
    {
        this.applicationContext = applicationContext;
        this.persistenceContext = persistenceContext;
        this.plansSummaryCalculator = plansSummaryCalculator;
        this.setYearAndMonth(DateTimeHelper.getCurrentYear(), DateTimeHelper.getCurrentMonth());
    }

    @Bindable
    public MonthlyPlanningCategoryListViewModel getMonthlyPlans()
    {
        return this.monthlyPlansViewModel;
    }

    @Bindable
    public int getYear()
    {
        return this.monthlyPlansViewModel.getMonthData().getYear();
    }

    public void setYear(int newYear)
    {
        this.setYearAndMonth(newYear, this.getMonth());
    }

    @Bindable
    public String getMonthString()
    {
        return ResourcesHelper.toResourceString(applicationContext.getDefaultContext(), this.getMonth());
    }

    public void setMonthString(String newMonthString)
    {
        this.setMonth(ResourcesHelper.monthFromResourceString(this.applicationContext.getDefaultContext(), newMonthString));
    }

    @Bindable
    public Month getMonth()
    {
        return this.monthlyPlansViewModel.getMonthData().getMonth();
    }

    public void setMonth(Month newMonth)
    {
        this.setYearAndMonth(this.getYear(), newMonth);
    }

    private void setYearAndMonth(int year, Month month) {

        boolean notifyMonthChanged = false;
        boolean notifyYearChanged = false;
        if(this.monthlyPlansViewModel == null
                || this.monthlyPlansViewModel.getMonthData() != null
                    && ((notifyMonthChanged = this.monthlyPlansViewModel.getMonthData().getYear() != year)
                            || (notifyYearChanged = this.monthlyPlansViewModel.getMonthData().getMonth() != month)))
        {
            YearMonthPair yearMonthPair = new YearMonthPair(year, month);
            if(!this.viewModelMap.containsKey(yearMonthPair))
            {
                MonthlyPlansModel model = this.getMonthlyPlans(yearMonthPair);
                this.viewModelMap.put(yearMonthPair, new MonthlyPlanningCategoryListViewModel(model, this.plansSummaryCalculator));
            }

            this.monthlyPlansViewModel = this.viewModelMap.get(yearMonthPair);
            this.notifyPropertyChanged(BR.monthlyPlans);
            if(notifyMonthChanged) {
                this.notifyPropertyChanged(BR.month);
                this.notifyPropertyChanged(BR.monthString);
            }

            if(notifyYearChanged)
                this.notifyPropertyChanged(BR.year);
        }
    }

    private MonthlyPlansModel getMonthlyPlans(YearMonthPair yearMonthPair)
    {
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();

        MonthlyPlansModel model = uow.getMonthlyPlansRepository().findForMonth(yearMonthPair.getYear(), yearMonthPair.getMonth());
        if(model == null)
        {
            model = new MonthlyPlansModel(yearMonthPair.hashCode(), yearMonthPair.getYear(), yearMonthPair.getMonth());
            uow.getMonthlyPlansRepository().add(model);
        }

        uow.complete();

        return model;
    }

}
