package com.slamcode.goalcalendar.data.json;

import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 03.01.2017.
 */

public final class JsonDataBundle {

    List<MonthlyPlansModel> monthlyPlans;

    JsonDataBundle()
    {
        this.monthlyPlans = new ArrayList<>();
    }
}
