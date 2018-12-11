package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.DataFormatter;

public class BackupJsonFilePersistenceContext extends JsonFilePersistenceContext implements BackupPersistenceContext {

    public BackupJsonFilePersistenceContext(Context appContext, DataFormatter<JsonMonthlyPlansDataBundle> dataFormatter, String fileName) {
        super(appContext, dataFormatter, fileName);
    }
}
