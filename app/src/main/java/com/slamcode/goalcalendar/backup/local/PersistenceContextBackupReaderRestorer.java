package com.slamcode.goalcalendar.backup.local;

import com.slamcode.goalcalendar.backup.BackupRestorer;
import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public final class PersistenceContextBackupReaderRestorer implements BackupWriter, BackupRestorer {

    private final ModelInfoProvider modelInfoProvider;
    private final MainPersistenceContext mainPersistenceContext;
    private final BackupPersistenceContext backupPersistenceContext;

    public PersistenceContextBackupReaderRestorer(ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext, BackupPersistenceContext backupPersistenceContext)
    {
        this.modelInfoProvider = modelInfoProvider;
        this.mainPersistenceContext = mainPersistenceContext;
        this.backupPersistenceContext = backupPersistenceContext;
    }

    @Override
    public WriteResult writeBackup() {

        MonthlyPlansDataBundle mainData = this.mainPersistenceContext.getDataBundle();
        if(mainData == null)
            return new LocalBackupWriteResult(false, null, null);

        this.backupPersistenceContext.setDataBundle(mainData);
        this.backupPersistenceContext.persistData();

        BackupInfoModel result = new BackupInfoModel();
        result.setBackupDateUtc(new Date());
        result.setVersion(this.modelInfoProvider.getModelVersion());
        result.setSourceType("LOCAL");
        result.setId(UUID.randomUUID());
        return new LocalBackupWriteResult(true, result, null);
    }

    @Override
    public RestoreResult restoreBackup(BackupInfoModel backupInfo) {
        return null;
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
}
