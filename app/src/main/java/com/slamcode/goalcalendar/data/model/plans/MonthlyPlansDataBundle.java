package com.slamcode.goalcalendar.data.model.plans;

import com.slamcode.goalcalendar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for general monthly plans data
 */
public abstract class MonthlyPlansDataBundle {

    public List<MonthlyPlansModel> monthlyPlans;

    public int version;

    public MonthlyPlansDataBundle()
    {
        this.monthlyPlans = new ArrayList<>();
        this.version = R.integer.add_data_version;
    }
}
