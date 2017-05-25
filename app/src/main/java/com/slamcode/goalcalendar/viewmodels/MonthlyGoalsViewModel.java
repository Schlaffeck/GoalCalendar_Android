package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.*;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;
import com.slamcode.goalcalendar.view.utils.ResourcesHelper;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by moriasla on 02.03.2017.
 */

public class MonthlyGoalsViewModel extends BaseObservable {

    private final ApplicationContext applicationContext;
    private final PersistenceContext persistenceContext;
    private final PlansSummaryCalculator plansSummaryCalculator;
    private final SourceChangeRequestNotifier.SourceChangeRequestListener<CategoryPlansViewModel> categoryChangeRequestListener;
    private final SourceChangeRequestNotifier.SourceChangeRequestListener<DailyPlansViewModel> dailyPlansChangeRequestListener;
    private MonthlyPlanningCategoryListViewModel monthlyPlansViewModel;

    Map<YearMonthPair, MonthlyPlanningCategoryListViewModel> viewModelMap = new HashMap<>();

    public MonthlyGoalsViewModel(ApplicationContext applicationContext,
                                 PersistenceContext persistenceContext,
                                 PlansSummaryCalculator plansSummaryCalculator,
                                 SourceChangeRequestNotifier.SourceChangeRequestListener<CategoryPlansViewModel> categoryChangeRequestListener,
                                 SourceChangeRequestNotifier.SourceChangeRequestListener<DailyPlansViewModel> dailyPlansChangeRequestListener)
    {
        this.applicationContext = applicationContext;
        this.persistenceContext = persistenceContext;
        this.plansSummaryCalculator = plansSummaryCalculator;
        this.categoryChangeRequestListener = categoryChangeRequestListener;
        this.dailyPlansChangeRequestListener = dailyPlansChangeRequestListener;
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
                    && ((notifyYearChanged = this.monthlyPlansViewModel.getMonthData().getYear() != year)
                            || (notifyMonthChanged = this.monthlyPlansViewModel.getMonthData().getMonth() != month)))
        {
            YearMonthPair yearMonthPair = new YearMonthPair(year, month);
            if(!this.viewModelMap.containsKey(yearMonthPair))
            {
                MonthlyPlansModel model = this.getMonthlyPlans(yearMonthPair);
                this.viewModelMap.put(yearMonthPair,
                        new MonthlyPlanningCategoryListViewModel(
                                model,
                                this.plansSummaryCalculator,
                                this.categoryChangeRequestListener,
                                this.dailyPlansChangeRequestListener));
            }

            this.monthlyPlansViewModel = this.viewModelMap.get(yearMonthPair);
            this.notifyPropertyChanged(BR.monthlyPlans);
            this.notifyPropertyChanged(BR.previousMonthWithCategoriesAvailable);
            if(notifyMonthChanged) {
                this.notifyPropertyChanged(BR.month);
                this.notifyPropertyChanged(BR.monthString);
            }

            if(notifyYearChanged)
                this.notifyPropertyChanged(BR.year);
        }
    }

    @Bindable
    public boolean isPreviousMonthWithCategoriesAvailable()
    {
        return this.findPreviousMonthlyPlansModelWithCategories() != null;
    }

    private MonthlyPlansModel findPreviousMonthlyPlansModelWithCategories()
    {
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();
        MonthlyPlansModel previousMonthWithCategories = uow.getMonthlyPlansRepository().findLast(new Predicate<MonthlyPlansModel>() {
            @Override
            public boolean apply(MonthlyPlansModel monthlyPlansModel) {
                if(monthlyPlansModel.getMonth().getNumValue() < monthlyPlansModel.getMonth().getNumValue())
                {
                    return false;
                }

                return !monthlyPlansModel.getCategories().isEmpty();
            }
        });
        uow.complete(false);

        return previousMonthWithCategories;
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
