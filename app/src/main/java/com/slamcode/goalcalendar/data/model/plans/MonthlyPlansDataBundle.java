package com.slamcode.goalcalendar.data.model.plans;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.DataBundleAbstract;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for general monthly plans data
 */
public abstract class MonthlyPlansDataBundle extends DataBundleAbstract {

    public List<MonthlyPlansModel> monthlyPlans;

    public MonthlyPlansDataBundle()
    {
        this.monthlyPlans = new ArrayList<>();
        this.version = R.integer.add_data_version;
    }
}
