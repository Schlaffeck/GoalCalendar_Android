package com.slamcode.goalcalendar.backup.local;

import com.slamcode.goalcalendar.backup.BackupRestorer;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.Locale;
import java.util.UUID;

public final class PersistenceContextBackupSourceDataProvider implements BackupSourceDataProvider {

    public static final String SOURCE_TYPE = "LOCAL_JSON";
    private final AppSettingsManager appSettingsManager;
    private final PersistenceContextBackupReaderRestorer backupReaderRestorer;

    public PersistenceContextBackupSourceDataProvider(AppSettingsManager appSettingsManager, PersistenceContextBackupReaderRestorer backupReaderRestorer)
    {
        this.appSettingsManager = appSettingsManager;
        this.backupReaderRestorer = backupReaderRestorer;
    }

    @Override
    public String getSourceType() {
        return SOURCE_TYPE;
    }

    @Override
    public String getUserSourceIdentifier() {
        String identifier = this.appSettingsManager.getUserLocalIdentifier();
        if(identifier == null)
        {
            identifier = UUID.randomUUID().toString();
            this.appSettingsManager.setUserLocalIdentifier(identifier);
        }
        return identifier;
    }

    @Override
    public SourceDisplayData getDisplayData(Locale locale) {
        return new SourceDisplayData("Local");
    }

    @Override
    public BackupWriter getBackupWriter() {
        return this.backupReaderRestorer;
    }

    @Override
    public BackupRestorer getBackupRestorer() {
        return this.backupReaderRestorer;
    }
}
