package com.slamcode.goalcalendar.data.json;

import android.content.Context;

import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;

public class MainJsonFilePersistenceContext extends JsonFilePersistenceContext implements MainPersistenceContext {

    public MainJsonFilePersistenceContext(Context appContext, DataFormatter<MonthlyPlansDataBundle> dataFormatter, String fileName) {
        super(appContext, dataFormatter, fileName);
    }
}
