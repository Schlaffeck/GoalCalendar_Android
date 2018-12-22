package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.MainPersistenceContext;

import javax.inject.Named;

public class MainJsonFilePersistenceContext extends JsonFilePersistenceContext implements MainPersistenceContext {

    public static final String CONFIGURATION_NAME = "MAIN_JSON_CONFIG";

    public MainJsonFilePersistenceContext(Context appContext, DataFormatter dataFormatter, @Named(CONFIGURATION_NAME) JsonPersistenceContextConfiguration persistenceContextConfiguration) {
        super(appContext, dataFormatter, persistenceContextConfiguration);
    }
}
