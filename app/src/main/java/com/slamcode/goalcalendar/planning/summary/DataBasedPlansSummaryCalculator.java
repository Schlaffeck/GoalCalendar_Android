package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;

/**
 * Summary calculator based on persisted model data
 */

public class DataBasedPlansSummaryCalculator implements PlansSummaryCalculator {

    private final CategoriesRepository categoriesRepository;

    public DataBasedPlansSummaryCalculator(CategoriesRepository categoriesRepository)
    {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public PlansSummary calculatePlansSummaryForMonth(int year, Month month) {
        return calculateMultipleCategoriesSummary(this.categoriesRepository.findForMonth(year, month));
    }

    @Override
    public PlansSummary calculatePlansSummaryForDay(int year, Month month, int day) {
        return null;
    }

    @Override
    public PlansSummary calculatePlansSummaryForMonthInCategory(int year, Month month, String categoryName) {
        return calculateMultipleCategoriesSummary(this.categoriesRepository.findForMonthWithName(year, month, categoryName));
    }

    private PlansSummary calculateMultipleCategoriesSummary(Iterable<CategoryModel> categories)
    {
        PlansSummary summary = null;

        for(CategoryModel categoryModel : categories)
        {
            PlansSummary categorySummary = calculateCategorySummary(categoryModel);
            if(summary == null)
                summary = categorySummary;
            else if(categorySummary.dataAvailable)
            {
                // add next summary to current one
                summary.dataAvailable = true;
                summary.noOfExpectedTasks += categorySummary.noOfExpectedTasks;
                summary.noOfFailedTasks += categorySummary.noOfFailedTasks;
                summary.noOfSuccessfulTasks += categorySummary.noOfSuccessfulTasks;
            }
        }

        return summary != null ? summary : new PlansSummary();
    }

    private PlansSummary calculateCategorySummary(CategoryModel categoryModel)
    {
        PlansSummary categorySummary = new PlansSummary();
        categorySummary.noOfExpectedTasks = categoryModel.getFrequencyValue() *
                (categoryModel.getPeriod() == FrequencyPeriod.Month ? 1 : 4);

        if(categorySummary.noOfExpectedTasks < 0)
            categorySummary.noOfExpectedTasks = 0;

        categorySummary.dataAvailable = categorySummary.noOfExpectedTasks > 0;

        if(categorySummary.dataAvailable)
        {
            for (DailyPlanModel dailyPlan : categoryModel.getDailyPlans()) {
                switch(dailyPlan.getStatus())
                {
                    case Failure:
                        categorySummary.noOfFailedTasks++;
                        break;
                    case Success:
                        categorySummary.noOfSuccessfulTasks++;
                        break;
                }
            }
        }

        return categorySummary;
    }
}
