package com.slamcode.goalcalendar.backup.local;

import com.slamcode.goalcalendar.backup.BackupRestorer;
import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.model.backup.BackupDataBundle;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public final class PersistenceContextBackupWriterRestorer implements BackupWriter, BackupRestorer {

    private final ModelInfoProvider modelInfoProvider;
    private final MainPersistenceContext mainPersistenceContext;
    private final BackupPersistenceContext backupPersistenceContext;

    public PersistenceContextBackupWriterRestorer(ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext, BackupPersistenceContext backupPersistenceContext)
    {
        this.modelInfoProvider = modelInfoProvider;
        this.mainPersistenceContext = mainPersistenceContext;
        this.backupPersistenceContext = backupPersistenceContext;
    }

    @Override
    public WriteResult writeBackup() {

        MonthlyPlansDataBundle mainData = this.mainPersistenceContext.getDataBundle();
        BackupDataBundle backupData = this.mainPersistenceContext.getBackupDataBundle();
        if(mainData == null || backupData == null)
            return new LocalBackupWriteResult(false, null, null);

        this.backupPersistenceContext.setDataBundles(mainData, backupData);
        if(this.backupPersistenceContext.persistData()) {
            BackupInfoModel result = new BackupInfoModel();
            result.setBackupDateUtc(new Date()); // TODO: use UTC date
            result.setVersion(this.modelInfoProvider.getModelVersion());
            result.setSourceType(PersistenceContextBackupSourceDataProvider.SOURCE_TYPE);
            result.setId(UUID.randomUUID());
            return new LocalBackupWriteResult(true, result, null);
        }

        return new LocalBackupWriteResult(false, null, null);
    }

    @Override
    public RestoreResult restoreBackup(BackupInfoModel backupInfo) {
        this.backupPersistenceContext.initializePersistedData();
        MonthlyPlansDataBundle mainData = this.backupPersistenceContext.getDataBundle();
        BackupDataBundle backupData = this.backupPersistenceContext.getBackupDataBundle();
        if(mainData == null || backupData == null)
            return new LocalBackupRestoreResult(false, null);

        this.mainPersistenceContext.setDataBundles(mainData, backupData);
        return new LocalBackupRestoreResult(this.mainPersistenceContext.persistData(), null);
    }

    private class LocalBackupWriteResult implements BackupWriter.WriteResult{

        private boolean success;
        private BackupInfoModel model;
        private String message;

        private LocalBackupWriteResult(boolean success, BackupInfoModel model, String message) {
            this.success = success;
            this.model = model;
            this.message = message;
        }

        @Override
        public boolean isSuccess() {
            return this.success;
        }

        @Override
        public BackupInfoModel getInfoModel() {
            return this.model;
        }

        @Override
        public String getDetailedMessage(Locale locale) {
            return this.message;
        }
    }

    private class LocalBackupRestoreResult implements BackupRestorer.RestoreResult
    {
        private final boolean success;
        private final String message;

        private LocalBackupRestoreResult(boolean success, String message)
        {
            this.success = success;
            this.message = message;
        }

        @Override
        public boolean isSuccess() {
            return this.success;
        }

        @Override
        public String getDetailedMessage(Locale locale) {
            return this.message;
        }
    }
}
