package com.slamcode.goalcalendar.data.json.formatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.json.JsonMonthlyPlansDataBundle;

public class JsonDataFormatter implements DataFormatter<JsonMonthlyPlansDataBundle> {

    private final Gson gson;

    public JsonDataFormatter()
    {
        this.gson = new GsonBuilder()
            .create();
    }

    @Override
    public String formatDataBundle(JsonMonthlyPlansDataBundle dataBundle) {
        return gson.toJson(dataBundle, JsonMonthlyPlansDataBundle.class);
    }
}
