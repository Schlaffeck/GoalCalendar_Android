package com.slamcode.goalcalendar.data.json.formatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;

public class JsonDataFormatter implements DataFormatter<MonthlyPlansDataBundle> {

    private final Gson gson;

    public JsonDataFormatter()
    {
        this.gson = new GsonBuilder()
            .create();
    }

    @Override
    public String formatDataBundle(MonthlyPlansDataBundle dataBundle) {
        return gson.toJson(dataBundle, MonthlyPlansDataBundle.class);
    }
}
