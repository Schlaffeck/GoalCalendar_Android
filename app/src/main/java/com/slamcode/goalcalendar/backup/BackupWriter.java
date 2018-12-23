package com.slamcode.goalcalendar.backup;

import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.Locale;

public interface BackupWriter {

    WriteResult writeBackup();

    interface WriteResult
    {
        boolean isSuccess();

        BackupInfoModel getInfoModel();

        String getDetailedMessage(Locale locale);
    }
}