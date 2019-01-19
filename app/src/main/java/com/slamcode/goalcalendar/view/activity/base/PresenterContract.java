package com.slamcode.goalcalendar.view.activity.base;

import android.databinding.BaseObservable;

import com.slamcode.goalcalendar.view.activity.base.ActivityViewContract;

public interface PresenterContract<ViewModel extends BaseObservable, ActivityView extends ActivityViewContract<ViewModel>>
{
    void setData(ViewModel data);

    void initializeWithView(ActivityView activityView);
}
