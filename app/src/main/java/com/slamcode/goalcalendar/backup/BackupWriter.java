package com.slamcode.goalcalendar.backup;

import android.arch.core.util.Function;

import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.Locale;

public interface BackupWriter {

    void writeBackup(Function<WriteResult, WriteResult> callback);

    interface WriteResult
    {
        boolean isSuccess();

        BackupInfoModel getInfoModel();

        String getDetailedMessage(Locale locale);
    }
}
