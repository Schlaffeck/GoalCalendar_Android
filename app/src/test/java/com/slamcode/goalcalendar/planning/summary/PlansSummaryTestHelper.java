package com.slamcode.goalcalendar.planning.summary;

/**
 * Created by smoriak on 23/05/2017.
 */

public final class PlansSummaryTestHelper {

    public static PlansSummaryCalculator.CategoryPlansSummary createCategoryPlansSummary(String categoryName, int total, int done, int failed)
    {
        PlansSummaryCalculator.CategoryPlansSummary result = new PlansSummaryCalculator.CategoryPlansSummary(categoryName);
        result.dataAvailable = true;
        result.noOfExpectedTasks = total;
        result.noOfSuccessfulTasks = done;
        result.noOfFailedTasks = failed;

        return result;
    }
}
