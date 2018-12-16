package com.slamcode.goalcalendar.backup;

import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

public interface BackupWriter {

    BackupInfoModel writeBackup();
}
