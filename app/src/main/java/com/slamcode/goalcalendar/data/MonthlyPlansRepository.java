package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.Month;

/**
 * Created by moriasla on 23.12.2016.
 */

public interface MonthlyPlansRepository extends Repository<MonthlyPlansModel, Integer> {

    MonthlyPlansModel findForMonth(Month month, int year);

    MonthlyPlansModel findForThisMonth();

    MonthlyPlansModel findForNextMonth();

    MonthlyPlansModel findForPreviousMonth();
}
