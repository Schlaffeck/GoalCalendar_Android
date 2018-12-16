package com.slamcode.goalcalendar.data.model.plans;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.DataBundleAbstract;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 03.01.2017.
 */

public final class MonthlyPlansDataBundle extends DataBundleAbstract {

        public List<MonthlyPlansModel> monthlyPlans;

        public MonthlyPlansDataBundle()
        {
            this.monthlyPlans = new ArrayList<>();
            this.version = R.integer.add_data_version;
        }
    }
