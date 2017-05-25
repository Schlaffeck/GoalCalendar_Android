package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.view.View;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;

import java.util.ArrayList;
import java.util.List;

/**
 * View model for single daily plans
 */

public class DailyPlansViewModel extends BaseObservable implements Comparable<DailyPlansViewModel>, SourceChangeRequestNotifier<DailyPlansViewModel> {

    public static final int REQUEST_EDIT_DAILY_PLANS = 282873821;

    DailyPlanModel model;

    private List<SourceChangeRequestListener<DailyPlansViewModel>> sourceChangeRequestListeners = new ArrayList<>();

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

    @Bindable
    public int getDayNumber() {
        return this.model.getDayNumber();
    }

    @Bindable
    public String getDescription() {
        return this.model.getDescription();
    }

    public void changeStatus(Object param)
    {
        this.setStatus(this.getStatus().nextStatus());
    }

    @Override
    public int compareTo(@NonNull DailyPlansViewModel o) {

        if(o == null)
            return 1;

        return this.getDayNumber() > o.getDayNumber() ? 1
                : this.getDayNumber() < o.getDayNumber() ? -1 : 0;
    }

    public void setDescription(String description) {
        if(description != this.getDescription()) {
            this.model.setDescription(description);
            this.notifyPropertyChanged(BR.description);
        }
    }

    @Override
    public void addSourceChangeRequestListener(SourceChangeRequestListener<DailyPlansViewModel> listener) {
        this.sourceChangeRequestListeners.add(listener);
    }

    @Override
    public void removeSourceChangeRequestListener(SourceChangeRequestListener<DailyPlansViewModel> listener) {
        this.sourceChangeRequestListeners.remove(listener);
    }

    @Override
    public void clearSourceChangeRequestListeners() {
        this.sourceChangeRequestListeners.clear();
    }

    @Override
    public void notifySourceChangeRequested(SourceChangeRequest request) {
        for(SourceChangeRequestListener<DailyPlansViewModel> listener : this.sourceChangeRequestListeners)
        {
            listener.sourceChangeRequested(this, request);
        }
    }
}
