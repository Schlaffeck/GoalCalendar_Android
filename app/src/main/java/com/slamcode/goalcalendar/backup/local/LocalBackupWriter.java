package com.slamcode.goalcalendar.backup.local;

import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.json.JsonMonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;

import java.util.Date;

/**
 * Interface for saving backup data in local application storage
 */
public final class LocalBackupWriter implements BackupWriter<JsonMonthlyPlansDataBundle> {

    private final DataFormatter dataFormatter;
    private final ModelInfoProvider modelInfoProvider;

    public LocalBackupWriter(DataFormatter dataFormatter, ModelInfoProvider modelInfoProvider)
    {
        this.dataFormatter = dataFormatter;
        this.modelInfoProvider = modelInfoProvider;
    }

    @Override
    public BackupInfoModel writeBackup(JsonMonthlyPlansDataBundle dataToBackup) {
        Date backupDate = new Date();
        String formatted = this.dataFormatter.formatDataBundle(dataToBackup);
        // TODO: save data
        BackupInfoModel result = new BackupInfoModel();
        result.setVersion(this.modelInfoProvider.getModelVersion());
        result.setBackupDateUtc(backupDate);
        return result;
    }
}
