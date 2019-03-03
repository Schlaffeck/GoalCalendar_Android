package com.slamcode.goalcalendar.backup.azure.service;

import com.slamcode.goalcalendar.data.model.backup.BackupDataBundle;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;

import java.util.concurrent.Future;

import retrofit2.Callback;

public interface AzureService {

    void postBackupData(PostBackupDataRequest request);

    void getBackupData(GetBackupDataRequest request, Callback<BackupData> callback);

    class PostBackupDataRequest extends BackupData
    {
    }

    class BackupData
    {
        public int modelVersion;

        public String userId;

        public MonthlyPlansDataBundle monthlyPlans;

        public BackupDataBundle backupInfos;
    }

    class GetBackupDataRequest
    {
        public int modelVersion;

        public String userId;
    }
}
