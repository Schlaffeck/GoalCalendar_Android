package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.planning.Month;

/**
 * Interface for general objects calculating
 * plans progress and numerical values describing it
 */

public interface PlansSummaryCalculator {

    /**
     * Calculates summary of planned and actually executed tasks in given month in all categories
     * @param year
     * @param month
     * @return Plans summary calculation result
     */
    PlansSummary calculatePlansSummaryForMonth(int year, Month month);

    /**
     * Calculates summary of planned and actually executed tasks in given day of month
     * in all categories
     * @param year
     * @param month
     * @param day
     * @return Plans summary calculation result
     */
    PlansSummary calculatePlansSummaryForDay(int year, Month month, int day);

    /**
     * Calculates summary of planned and actually executed tasks in given month in one category
     * @param year
     * @param month
     * @param categoryName
     * @return Plans summary calculation result
     */
    PlansSummary calculatePlansSummaryForMonthInCategory(int year, Month month, String categoryName);

    /**
     * Basic summary calculation result holder.
     */
    class PlansSummary
    {
        protected boolean dataAvailable;

        protected int noOfExpectedTasks;

        protected int noOfSuccessfulTasks;

        protected int noOfFailedTasks;

        public boolean getDataAvailable() {
            return this.dataAvailable;
        }

        public double countProgressPercentage()
        {
            if(!dataAvailable || noOfExpectedTasks ==  0)
                return 0;
            return (this.noOfSuccessfulTasks * 1.0) / (this.noOfExpectedTasks * 1.0) * 100;
        }

        public double getNoOfExpectedTasks() {
            return this.noOfExpectedTasks;
        }

        public double getNoOfSuccessfulTasks() {
            return this.noOfSuccessfulTasks;
        }

        public double getNoOfFailedTasks() {
            return this.noOfFailedTasks;
        }

    }
}
