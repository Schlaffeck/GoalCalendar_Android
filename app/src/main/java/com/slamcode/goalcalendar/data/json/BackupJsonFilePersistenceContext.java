package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.slamcode.goalcalendar.data.BackupPersistenceContext;

public class BackupJsonFilePersistenceContext extends JsonFilePersistenceContext implements BackupPersistenceContext {

    public BackupJsonFilePersistenceContext(Context appContext, String fileName) {
        super(appContext, fileName);
    }
}
