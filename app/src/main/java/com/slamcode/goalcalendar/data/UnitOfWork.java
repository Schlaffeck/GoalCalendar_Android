package com.slamcode.goalcalendar.data;

/**
 * Created by moriasla on 03.01.2017.
 */

public interface UnitOfWork  {

    CategoriesRepository getCategoriesRepository();

    MonthlyPlansRepository getMonthlyPlansRepository();

    /**
     * Completes unit of work with persisting data by default
     */
    void complete();

    /**
     * Completes unit of work with developer choosing whetehr to persis data or not
     * @param persistData
     */
    void complete(boolean persistData);
}
