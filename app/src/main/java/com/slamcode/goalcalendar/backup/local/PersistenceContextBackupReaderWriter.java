package com.slamcode.goalcalendar.backup.local;

import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.Date;
import java.util.UUID;

public final class PersistenceContextBackupReaderWriter implements BackupWriter {

    private final ModelInfoProvider modelInfoProvider;
    private final MainPersistenceContext mainPersistenceContext;
    private final BackupPersistenceContext backupPersistenceContext;

    public PersistenceContextBackupReaderWriter(ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext, BackupPersistenceContext backupPersistenceContext)
    {
        this.modelInfoProvider = modelInfoProvider;
        this.mainPersistenceContext = mainPersistenceContext;
        this.backupPersistenceContext = backupPersistenceContext;
    }

    @Override
    public BackupInfoModel writeBackup() {

        MonthlyPlansDataBundle mainData = this.mainPersistenceContext.getDataBundle();
        if(mainData == null)
            return null;

        this.backupPersistenceContext.setDataBundle(mainData);
        this.backupPersistenceContext.persistData();

        BackupInfoModel result = new BackupInfoModel();
        result.setBackupDateUtc(new Date());
        result.setVersion(this.modelInfoProvider.getModelVersion());
        result.setSourceType("LOCAL");
        result.setId(UUID.randomUUID());
        return result;
    }
}
