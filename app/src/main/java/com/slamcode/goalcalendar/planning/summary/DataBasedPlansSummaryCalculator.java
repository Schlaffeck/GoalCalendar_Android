package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;

import java.util.List;

/**
 * Summary calculator based on persisted model data
 */

public class DataBasedPlansSummaryCalculator implements PlansSummaryCalculator {

    private final CategoriesRepository categoriesRepository;
    private final PlansSummaryDescriptionProvider descriptionProvider;

    public DataBasedPlansSummaryCalculator(CategoriesRepository categoriesRepository, PlansSummaryDescriptionProvider descriptionProvider)
    {
        this.categoriesRepository = categoriesRepository;
        this.descriptionProvider = descriptionProvider;
    }

    @Override
    public MonthPlansSummary calculatePlansSummaryForMonth(int year, Month month) {
        MonthPlansSummary plansSummary = new MonthPlansSummary(year, month);
        calculateMultipleCategoriesSummary(plansSummary, this.categoriesRepository.findForMonth(year, month));
        plansSummary.description = descriptionProvider.provideDescriptionForMonth(year, month).getDetails();
        return plansSummary;
    }

    @Override
    public PlansSummary calculatePlansSummaryForDay(int year, Month month, int day) {
        return null;
    }

    @Override
    public CategoryPlansSummary calculatePlansSummaryForMonthInCategory(int year, Month month, String categoryName) {
        CategoryPlansSummary categoryPlansSummary = new CategoryPlansSummary(categoryName);
        calculateMultipleCategoriesSummary(categoryPlansSummary, this.categoriesRepository.findForMonthWithName(year, month, categoryName));
        categoryPlansSummary.description = descriptionProvider.provideDescriptionMonthInCategory(year, month, categoryName);
        return categoryPlansSummary;
    }

    private PlansSummary calculateMultipleCategoriesSummary(PlansSummary summary, Iterable<CategoryModel> categories)
    {
        for(CategoryModel categoryModel : categories)
        {
            CategoryPlansSummary categorySummary = calculateCategorySummary(categoryModel);
            if(categorySummary.dataAvailable)
            {
                // add next summary to current one
                summary.dataAvailable = true;
                summary.compositeSummaries.add(categorySummary);
                summary.noOfExpectedTasks += categorySummary.noOfExpectedTasks;
                summary.noOfFailedTasks += categorySummary.noOfFailedTasks;
                summary.noOfSuccessfulTasks += categorySummary.noOfSuccessfulTasks;
                summary.noOfPlannedTasks += categorySummary.noOfPlannedTasks;
            }
        }

        return summary;
    }

    private CategoryPlansSummary calculateCategorySummary(CategoryModel categoryModel)
    {
        CategoryPlansSummary categorySummary = new CategoryPlansSummary(categoryModel.getName());
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
                    case Planned:
                        categorySummary.noOfPlannedTasks++;
                        break;
                }
            }
        }

        return categorySummary;
    }
}
