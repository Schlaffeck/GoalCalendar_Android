package com.slamcode.goalcalendar.data.model;

import com.slamcode.collections.ElementCreator;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import java.util.List;

/**
 * Created by moriasla on 16.01.2017.
 */

public class ModelHelper {

    public static List<DailyPlanModel> createListOfDailyPlansForMonth(final int year, final Month month)
    {
        return com.slamcode.collections.CollectionUtils.createList(month.getDaysCount(year), new ElementCreator<DailyPlanModel>() {
            @Override
            public DailyPlanModel Create(int index, List<DailyPlanModel> currentList) {
                return new DailyPlanModel(index ^ month.getNumValue() ^ year, PlanStatus.Empty, index+1);
            }
        });
    }

    public static Predicate<DailyPlanModel> getDailyPlanIsOfStatusPredicate(final PlanStatus status, final int dayNumber)
    {
        return new Predicate<DailyPlanModel>() {
            @Override
            public boolean evaluate(DailyPlanModel dailyPlanModel) {
                return dailyPlanModel != null
                        && dailyPlanModel.getDayNumber() == dayNumber
                        && dailyPlanModel.getStatus() == status;
            }
        };
    }

    public static Predicate<CategoryModel> getCategoryOfStatusOnDayPredicate(final PlanStatus planStatus, final int dayNumber)
    {
        return new Predicate<CategoryModel>() {
            @Override
            public boolean evaluate(CategoryModel categoryModel) {
                if (categoryModel == null)
                    return false;

                if (categoryModel.getDailyPlans() == null
                        || categoryModel.getDailyPlans().isEmpty())
                    return false;

                final DailyPlanModel dailyPlanModel = IterableUtils.find(
                        categoryModel.getDailyPlans(),
                        ModelHelper.getDailyPlanIsOfStatusPredicate(planStatus, dayNumber));

                return dailyPlanModel != null;
            }
        };
    }

    public static int countNoOfExpectedTasks(CategoryModel categoryModel)
    {
        return categoryModel.getFrequencyValue() * (categoryModel.getPeriod() == FrequencyPeriod.Week ? 4 : 1);
    }

    public static int countNoOfTasksWithStatus(CategoryModel categoryModel, final PlanStatus status)
    {
        return com.slamcode.collections.CollectionUtils.sum(categoryModel.getDailyPlans(), new ElementSelector<DailyPlanModel, Integer>() {
            @Override
            public Integer select(DailyPlanModel parent) {
                return parent == null || parent.getStatus() != status ? 0 : 1;
            }
        });
    }
}
