package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.MainPersistenceContext;

public class MainJsonFilePersistenceContext extends JsonFilePersistenceContext implements MainPersistenceContext {

    public MainJsonFilePersistenceContext(Context appContext, DataFormatter<JsonMonthlyPlansDataBundle> dataFormatter, String fileName) {
        super(appContext, dataFormatter, fileName);
    }
}
