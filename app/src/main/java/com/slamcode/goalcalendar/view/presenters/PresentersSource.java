package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.view.activity.BackupActivityContract;
import com.slamcode.goalcalendar.view.activity.LoginActivityContract;
import com.slamcode.goalcalendar.view.activity.MonthlyGoalsActivityContract;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

/**
 * Created by moriasla on 24.02.2017.
 */

public interface PresentersSource {

    MonthlyGoalsPresenter getMonthlyGoalsPresenter(MonthlyGoalsActivityContract.ActivityView activityView);

    BackupPresenter getBackupPresenter(BackupActivityContract.ActivityView activityView);

    LoginPresenter getLoginPresenter(LoginActivityContract.ActivityView activityView);
}
