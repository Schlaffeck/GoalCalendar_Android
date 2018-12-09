package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.UUID;

public interface BackupInfoRepository extends Repository<BackupInfoModel, UUID> {

    BackupInfoModel getLatestBackupInfo();

    BackupInfoModel getLatestBackupInfo(String sourceType);
}
