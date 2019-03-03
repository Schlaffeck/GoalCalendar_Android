package com.slamcode.goalcalendar.backup.azure;

import android.arch.core.util.Function;

import com.slamcode.goalcalendar.backup.BackupRestorer;
import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.backup.azure.service.AzureService;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;
import com.slamcode.goalcalendar.data.model.backup.BackupDataBundle;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansModel;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AzureBackupWriterRestorer implements BackupWriter, BackupRestorer {

    private final AzureService service;
    private final ModelInfoProvider modelInfoProvider;
    private final MainPersistenceContext mainPersistenceContext;

    public AzureBackupWriterRestorer(AzureService service, ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext)
    {
        this.service = service;
        this.modelInfoProvider = modelInfoProvider;
        this.mainPersistenceContext = mainPersistenceContext;
    }

    @Override
    public void writeBackup(final Function<WriteResult, WriteResult> callback) {
        try {
            this.service.postBackupData(this.createPostBackupDataRequest())
            .done(new DoneCallback<Boolean>() {
                @Override
                public void onDone(Boolean result) {
                    if(result)
                    {
                        callback.apply(new AzureWriteResult(saveBackupInfoData()));
                    }
                    else
                    {
                        callback.apply(new AzureWriteResult("Can not save backup"));
                    }
                }
            })
            .fail(new FailCallback<AzureService.FailResult>() {
                @Override
                public void onFail(AzureService.FailResult result) {
                    callback.apply(new AzureWriteResult(result.detailedMessage));
                }
            });
        }
        catch(Exception e)
        {
            new AzureWriteResult(e.getMessage());
        }
    }

    @Override
    public void restoreBackup(final Function<RestoreResult, RestoreResult> callback) {
        try {

            final AzureService.GetBackupDataRequest request = new AzureService.GetBackupDataRequest();
            request.modelVersion = this.modelInfoProvider.getModelVersion();

            this.service.getBackupData(request)
            .done(new DoneCallback<AzureService.BackupData>() {
                @Override
                public void onDone(AzureService.BackupData result) {

                    if(result == null)
                        callback.apply(new AzureRestoreResult(false, "No backup data found"));
                    else {
                        try {
                            restoreBackupData(result);
                            callback.apply(new AzureRestoreResult(true));
                        }
                        catch (Exception e)
                        {
                            callback.apply(new AzureRestoreResult(false, e.getMessage()));
                        }
                    }
                }
            })
            .fail(new FailCallback<AzureService.FailResult>() {
                @Override
                public void onFail(AzureService.FailResult result) {
                    callback.apply(new AzureRestoreResult(false, result.detailedMessage));
                }
            });
        }
        catch(Exception e)
        {
            new AzureRestoreResult(false, e.getMessage());
        }
    }

    private AzureService.PostBackupDataRequest createPostBackupDataRequest()
    {
        UnitOfWork uow = this.mainPersistenceContext.createUnitOfWork(true);

        AzureService.PostBackupDataRequest data = new AzureService.PostBackupDataRequest();
        data.modelVersion = this.modelInfoProvider.getModelVersion();
        data.monthlyPlans = new MonthlyPlansDataBundle();
        data.monthlyPlans.monthlyPlans = uow.getMonthlyPlansRepository().findAll();

        data.backupInfos = new BackupDataBundle();
        data.backupInfos.setBackupInfos(uow.getBackupInfoRepository().findAll());

        uow.complete(false);

        return data;
    }

    private BackupInfoModel saveBackupInfoData()
    {
        UnitOfWork uow = this.mainPersistenceContext.createUnitOfWork();

        BackupInfoModel info = new BackupInfoModel();
        info.setId(UUID.randomUUID());
        info.setSourceType(AzureBackupSourceDataProvider.SOURCE_TYPE);
        info.setVersion(this.modelInfoProvider.getModelVersion());
        info.setBackupDateUtc(new Date()); // TODO: use UTC date

        uow.getBackupInfoRepository().add(info);

        uow.complete(true);
        return info;
    }

    private void restoreBackupData(AzureService.BackupData result)
    {
        UnitOfWork uow = this.mainPersistenceContext.createUnitOfWork();

        List<MonthlyPlansModel> newList = result.monthlyPlans.monthlyPlans;
        List<MonthlyPlansModel> list = uow.getMonthlyPlansRepository().findAll();

        for (MonthlyPlansModel model : list) {
            uow.getMonthlyPlansRepository().remove(model);
        }

        for (MonthlyPlansModel model : newList) {
            uow.getMonthlyPlansRepository().add(model);
        }

        uow.complete(true);
    }

    private class AzureRestoreResult implements RestoreResult
    {
        private String detailedMessage;
        private boolean success;

        AzureRestoreResult(boolean success) {
            this(success, null);
        }

        AzureRestoreResult(boolean success, String detailedMessage)
        {
            this.success = success;
            this.detailedMessage = detailedMessage;
        }

        public String getDetailedMessage(Locale locale) {
            return detailedMessage;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private class AzureWriteResult implements WriteResult
    {
        private boolean success;
        private  BackupInfoModel infoModel;
        private  String message;

        AzureWriteResult(BackupInfoModel modelSaved)
        {
            this.success = modelSaved != null;
            this.infoModel = modelSaved;
        }

        AzureWriteResult(String errorMessage)
        {
            this.success = false;
            this.message = errorMessage;
        }

        @Override
        public boolean isSuccess() {
            return this.success;
        }

        @Override
        public BackupInfoModel getInfoModel() {
            return this.infoModel;
        }

        @Override
        public String getDetailedMessage(Locale locale) {
            return this.message;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void setInfoModel(BackupInfoModel infoModel) {
            this.infoModel = infoModel;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
