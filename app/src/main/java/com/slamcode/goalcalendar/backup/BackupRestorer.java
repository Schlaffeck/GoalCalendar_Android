package com.slamcode.goalcalendar.backup;

import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.Locale;

public interface BackupRestorer {

    RestoreResult restoreBackup(BackupInfoModel backupInfo);

    interface RestoreResult{

        boolean getIsSuccess();

        String getDetailedMessage(Locale locale);
    }
}
