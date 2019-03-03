package com.slamcode.goalcalendar.backup.azure.service;

import com.slamcode.goalcalendar.data.model.backup.BackupDataBundle;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;

import org.jdeferred.Promise;

import java.util.concurrent.Future;

import retrofit2.Callback;

public interface AzureService {

    Promise<Boolean, FailResult, ProgressResult> postBackupData(PostBackupDataRequest request);

    Promise<BackupData, FailResult, ProgressResult> getBackupData(GetBackupDataRequest request);

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

    class FailResult
    {
        FailResult(Throwable exception, String detailedMessage)
        {
            this.exception = exception;
            this.detailedMessage = detailedMessage;
        }

        public Throwable exception;

        public String detailedMessage;
    }

    class ProgressResult
    {
        double progressPercent;

        public String progressMessage;
    }
}
