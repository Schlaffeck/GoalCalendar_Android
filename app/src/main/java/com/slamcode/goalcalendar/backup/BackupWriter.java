package com.slamcode.goalcalendar.backup;

import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

public interface BackupWriter<DataBundle extends MonthlyPlansDataBundle> {

    BackupInfoModel writeBackup(DataBundle dataToBackup);
}
