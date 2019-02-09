package com.slamcode.goalcalendar.backup.azure.service;

import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;
import com.slamcode.goalcalendar.data.model.plans.CategoryModel;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansModel;

import java.util.Collection;

public interface AzureService {

    void postBackupData(BackupData data);

    class BackupData
    {
        int modelVersion;

        String userId;

        public Collection<MonthlyPlansModel> monthlyPlans;

        public Collection<CategoryModel> categories;

        public Collection<BackupInfoModel> backupInfos;
    }
}
