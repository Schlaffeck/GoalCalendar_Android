package com.slamcode.goalcalendar.view.activity;

import com.slamcode.goalcalendar.android.StartForResult;
import com.slamcode.goalcalendar.view.activity.base.ActivityViewContract;
import com.slamcode.goalcalendar.view.activity.base.PresenterContract;
import com.slamcode.goalcalendar.viewmodels.LoginViewModel;

public interface LoginActivityContract {
    /**
     * Presenter contains actions for view elements, that can modify the data
     */
    public interface Presenter extends PresenterContract<LoginViewModel, ActivityView> {
    }

    /**
     * View has simple logic of managing binding presenters actions and data to concrete controls
     */
    public interface ActivityView extends ActivityViewContract<LoginViewModel>, StartForResult {
    }
}