package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.planning.Month;

/**
 * Created by smoriak on 29/05/2017.
 */

public interface PlansSummaryDescriptionProvider {

    PlansSummaryDescription provideDescriptionForMonth(int year, Month month);

    String provideDescriptionMonthInCategory(int year, Month month, String categoryName);

    /**
     * Represents detailed description object returned by interface methods
     */
    class PlansSummaryDescription {

        private String title;

        private String details;

        public PlansSummaryDescription()
        {
        }

        public PlansSummaryDescription(String title, String details)
        {
            this.title = title;
            this.details = details;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
