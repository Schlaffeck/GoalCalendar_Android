package com.slamcode.goalcalendar.backup.azure.service;

import com.slamcode.goalcalendar.data.model.backup.BackupDataBundle;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;
import com.slamcode.goalcalendar.data.model.plans.CategoryModel;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansModel;

import java.util.Collection;

public interface AzureService {

    void postBackupData(BackupData data);

    class BackupData
    {
        public int modelVersion;

        public String userId;

        public MonthlyPlansDataBundle monthlyPlans;

        public BackupDataBundle backupInfos;
    }
}
