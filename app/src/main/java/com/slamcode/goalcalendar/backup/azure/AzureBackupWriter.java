package com.slamcode.goalcalendar.backup.azure;

import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.backup.azure.service.AzureService;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;
import com.slamcode.goalcalendar.data.model.backup.BackupDataBundle;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AzureBackupWriter implements BackupWriter {

    private final AzureService service;
    private final ModelInfoProvider modelInfoProvider;
    private final MainPersistenceContext mainPersistenceContext;

    public AzureBackupWriter(AzureService service, ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext)
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
