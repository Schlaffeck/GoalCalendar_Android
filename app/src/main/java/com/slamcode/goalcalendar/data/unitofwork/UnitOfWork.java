package com.slamcode.goalcalendar.data.unitofwork;

import com.slamcode.goalcalendar.data.BackupInfoRepository;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;

/**
 * Created by moriasla on 03.01.2017.
 */

public interface UnitOfWork  {

    CategoriesRepository getCategoriesRepository();

    MonthlyPlansRepository getMonthlyPlansRepository();

    BackupInfoRepository getBackupInfoRepository();

    boolean isReadonly();

    /**
     * Completes unit of work with persisting data by default if unit of work is not readonly
     */
    void complete();

    /**
     * Completes unit of work with developer choosing whether to persis data or not
     * @param persistData
     */
    void complete(boolean persistData);
}
