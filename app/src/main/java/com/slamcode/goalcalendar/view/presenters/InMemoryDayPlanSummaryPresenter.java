package com.slamcode.goalcalendar.view.presenters;

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

public class InMemoryDayPlanSummaryPresenter implements DayPlanSummaryPresenter {

    private final MonthlyPlansModel monthlyPlansModel;
    private final int dayNumber;

    public InMemoryDayPlanSummaryPresenter(MonthlyPlansModel monthlyPlansModel, int dayNumber)
    {
        this.monthlyPlansModel = monthlyPlansModel;
        this.dayNumber = dayNumber;
    }

    @Override
    public Iterable<CategoryModel> getPlannedCategories()
    {
        return CollectionUtils.select(
                monthlyPlansModel.getCategories(),
                ModelHelper.getCategoryOfStatusOnDayPredicate(PlanStatus.Planned, this.dayNumber));
    }

    @Override
    public Iterable<CategoryModel> getSuccessfulCategories()
    {
        return CollectionUtils.select(
                monthlyPlansModel.getCategories(),
                ModelHelper.getCategoryOfStatusOnDayPredicate(PlanStatus.Success, this.dayNumber));
    }
}
