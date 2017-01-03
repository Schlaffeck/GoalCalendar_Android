package com.slamcode.goalcalendar.data;

/**
 * Created by moriasla on 03.01.2017.
 */

public interface UnitOfWork  {

    CategoryRepository getCategoryRepository();

    MonthlyPlansRepository getMonthlyPlansRepository();

    void complete();
}
