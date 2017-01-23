package com.slamcode.goalcalendar.view.viewmodel;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.List;

/**
 * Created by moriasla on 23.01.2017.
 */

public class DayPlanSummaryViewModel {

    private final MonthlyPlansModel monthlyPlansModel;
    private final int dayNumber;

    public DayPlanSummaryViewModel(MonthlyPlansModel monthlyPlansModel, int dayNumber)
    {
        this.monthlyPlansModel = monthlyPlansModel;
        this.dayNumber = dayNumber;
    }

    public Iterable<CategoryModel> getPlannedCategories()
    {
        return CollectionUtils.select(
                monthlyPlansModel.getCategories(),
                ModelHelper.getCategoryOfStatusOnDayPredicate(PlanStatus.Planned, this.dayNumber));
    }

    public Iterable<CategoryModel> getSuccessfulCategories()
    {
        return CollectionUtils.select(
                monthlyPlansModel.getCategories(),
                ModelHelper.getCategoryOfStatusOnDayPredicate(PlanStatus.Success, this.dayNumber));
    }
}
