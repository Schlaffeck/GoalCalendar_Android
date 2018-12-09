package com.slamcode.goalcalendar.backup.local;

import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

/**
 * Interface for saving backup data in local application storage
 */
public final class LocalBackupWriter implements BackupWriter {

    @Override
    public BackupInfoModel writeBackup() {
        return null;
    }
}
