package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.slamcode.goalcalendar.data.MainPersistenceContext;

public class MainJsonFilePersistenceContext extends JsonFilePersistenceContext implements MainPersistenceContext {

    public MainJsonFilePersistenceContext(Context appContext, String fileName) {
        super(appContext, fileName);
    }
}
