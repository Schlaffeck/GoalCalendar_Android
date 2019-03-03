package com.slamcode.goalcalendar.backup;

import android.arch.core.util.Function;

import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.Locale;

public interface BackupRestorer {

    void restoreBackup(Function<RestoreResult, RestoreResult> callback);

    interface RestoreResult{

        boolean isSuccess();

        String getDetailedMessage(Locale locale);
    }
}
