package com.slamcode.goalcalendar.viewmodels;

import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.planning.DateTime;

import java.util.Locale;

public class BackupSourceViewModel {

    private final BackupSourceDataProvider dataProvider;
    private final DateTime lastBackupDateTime;

    BackupSourceViewModel(BackupSourceDataProvider dataProvider, DateTime lastBackupDateTime)
    {
        this.dataProvider = dataProvider;
        this.lastBackupDateTime = lastBackupDateTime;
    }

    public String getName() {
        return this.dataProvider.getDisplayData(Locale.getDefault()).getSourceName();
    }

    public boolean isHasBackup() {
        return this.lastBackupDateTime != null;
    }

    public DateTime getLastBackupDateTime()
    {
        return this.lastBackupDateTime;
    }

    public String getSourceType()
    {
        return this.dataProvider.getSourceType();
    }
}
