package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;

public class BackupJsonFilePersistenceContext extends JsonFilePersistenceContext implements BackupPersistenceContext {

    public BackupJsonFilePersistenceContext(Context appContext, DataFormatter<MonthlyPlansDataBundle> dataFormatter, String fileName) {
        super(appContext, dataFormatter, fileName);
    }
}
