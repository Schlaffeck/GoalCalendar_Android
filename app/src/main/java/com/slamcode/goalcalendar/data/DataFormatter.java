package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;

public interface DataFormatter<DataBundle extends MonthlyPlansDataBundle> {

    String formatDataBundle(DataBundle dataBundle);
}
