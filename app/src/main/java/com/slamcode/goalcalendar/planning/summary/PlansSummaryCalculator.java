package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.planning.Month;

import java.util.ArrayList;
import java.util.Collection;

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
    MonthPlansSummary calculatePlansSummaryForMonth(int year, Month month);

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
    CategoryPlansSummary calculatePlansSummaryForMonthInCategory(int year, Month month, String categoryName);

    /**
     * Basic summary calculation result holder.
     */
    class PlansSummary
    {
        protected boolean dataAvailable;

        protected int noOfExpectedTasks;

        protected int noOfSuccessfulTasks;

        protected int noOfFailedTasks;

        protected Collection<PlansSummary> compositeSummaries = new ArrayList<>();

        public boolean getDataAvailable() {
            return this.dataAvailable;
        }

        public double countProgressPercentage()
        {
            if(!dataAvailable || noOfExpectedTasks ==  0)
                return 0;

            if(compositeSummaries != null && !compositeSummaries.isEmpty())
            {
                int allDoneTasks = 0;
                int allExpectedTasks = 0;
                for(PlansSummary summary : compositeSummaries)
                {
                    allExpectedTasks += summary.noOfExpectedTasks;
                    allDoneTasks += summary.noOfSuccessfulTasks > summary.noOfExpectedTasks
                            ? summary.noOfExpectedTasks : summary.noOfSuccessfulTasks;
                }

                return ((allDoneTasks * 1.0) / (allExpectedTasks * 1.0)) * 100.00;
            }

            return Math.min(100.0, (this.noOfSuccessfulTasks * 1.0) / (this.noOfExpectedTasks * 1.0) * 100);
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

    class CategoryPlansSummary extends PlansSummary
    {
        private final String categoryName;

        CategoryPlansSummary(String categoryName)
        {
            this.categoryName = categoryName;
        }

        public String getCategoryName() {
            return categoryName;
        }
    }

    class MonthPlansSummary extends PlansSummary
    {
        private final int year;
        private final Month month;

        MonthPlansSummary(int year, Month month)
        {
            this.year = year;
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public Month getMonth() {
            return month;
        }
    }
}
