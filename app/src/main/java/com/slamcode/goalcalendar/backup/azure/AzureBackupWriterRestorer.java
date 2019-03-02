package com.slamcode.goalcalendar.backup.azure;

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
    public WriteResult writeBackup() {
        BackupInfoModel info = null;
        try {
            UnitOfWork uow = this.mainPersistenceContext.createUnitOfWork();

            AzureService.PostBackupDataRequest data = new AzureService.PostBackupDataRequest();
            data.modelVersion = this.modelInfoProvider.getModelVersion();
            data.monthlyPlans = new MonthlyPlansDataBundle();
            data.monthlyPlans.monthlyPlans = uow.getMonthlyPlansRepository().findAll();

            data.backupInfos = new BackupDataBundle();
            data.backupInfos.setBackupInfos(uow.getBackupInfoRepository().findAll());

            this.service.postBackupData(data);

            info = new BackupInfoModel();
            info.setId(UUID.randomUUID());
            info.setSourceType(AzureBackupSourceDataProvider.SOURCE_TYPE);
            info.setVersion(this.modelInfoProvider.getModelVersion());
            info.setBackupDateUtc(new Date()); // TODO: use UTC date

            uow.getBackupInfoRepository().add(info);

            uow.complete(true);
        }
        catch(Exception e)
        {
            new AzureWriteResult(e.getMessage());
        }

        return new AzureWriteResult(info);
    }

    @Override
    public AzureRestoreResult restoreBackup(BackupInfoModel backupInfo) {
        BackupInfoModel info = null;
        try {

            AzureService.GetBackupDataRequest request = new AzureService.GetBackupDataRequest();
            request.modelVersion = this.modelInfoProvider.getModelVersion();

            AzureService.BackupData getResult = this.service.getBackupData(request);
            if(getResult == null)
                return new AzureRestoreResult(false, "No backup data found");

            UnitOfWork uow = this.mainPersistenceContext.createUnitOfWork();

            List<MonthlyPlansModel> newList = getResult.monthlyPlans.monthlyPlans;
            List<MonthlyPlansModel> list = uow.getMonthlyPlansRepository().findAll();

            for (MonthlyPlansModel model : list) {
                uow.getMonthlyPlansRepository().remove(model);
            }

            for (MonthlyPlansModel model : newList) {
                uow.getMonthlyPlansRepository().add(model);
            }

            uow.complete(true);
        }
        catch(Exception e)
        {
            new AzureRestoreResult(false, e.getMessage());
        }

        return new AzureRestoreResult(true);
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
