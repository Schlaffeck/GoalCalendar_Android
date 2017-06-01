package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.planning.Month;

/**
 * Created by smoriak on 29/05/2017.
 */

public interface PlansSummaryDescriptionProvider {

    String provideDescriptionForMonth(int year, Month month);

    String provideDescriptionMonthInCategory(int year, Month month, String categoryName);
}
