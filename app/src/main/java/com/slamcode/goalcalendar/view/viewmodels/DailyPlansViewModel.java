package com.slamcode.goalcalendar.view.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.PlanStatus;

/**
 * View model for single daily plans
 */

public class DailyPlansViewModel extends BaseObservable {

    DailyPlanModel model;

    public DailyPlansViewModel(DailyPlanModel model)
    {
        this.model = model;
    }

    @Bindable
    public PlanStatus getStatus()
    {
        return this.model.getStatus();
    }

    public void setStatus(PlanStatus newStatus)
    {
        if(newStatus != this.model.getStatus()) {
            this.model.setStatus(newStatus);
            this.notifyPropertyChanged(BR.status);
        }
    }
}
