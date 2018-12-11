package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.Month;

/**
 * Created by moriasla on 23.12.2016.
 */

public interface MonthlyPlansRepository extends Repository<MonthlyPlansModel, Integer> {

    MonthlyPlansModel findForMonth(Month month);

    MonthlyPlansModel findForMonth(int year, Month month);

    MonthlyPlansModel findForThisMonth();

    MonthlyPlansModel findForNextMonth();

    MonthlyPlansModel findForPreviousMonth();
}
