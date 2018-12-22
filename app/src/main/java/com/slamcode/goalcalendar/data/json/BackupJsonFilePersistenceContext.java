package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;

import javax.inject.Inject;
import javax.inject.Named;

public class BackupJsonFilePersistenceContext extends JsonFilePersistenceContext implements BackupPersistenceContext {

    public static final String CONFIGURATION_NAME = "BACKUP_JSON_CONFIG";

    @Inject
    public BackupJsonFilePersistenceContext(Context appContext, DataFormatter dataFormatter, @Named(CONFIGURATION_NAME) JsonPersistenceContextConfiguration persistenceContextConfiguration) {
        super(appContext, dataFormatter, persistenceContextConfiguration);
    }
}
