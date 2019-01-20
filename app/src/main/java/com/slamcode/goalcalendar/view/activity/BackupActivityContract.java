package com.slamcode.goalcalendar.view.activity;

import com.slamcode.goalcalendar.android.StartForResult;
import com.slamcode.goalcalendar.view.activity.base.ActivityViewContract;
import com.slamcode.goalcalendar.view.activity.base.PresenterContract;
import com.slamcode.goalcalendar.viewmodels.BackupViewModel;

public interface BackupActivityContract {

    int SHOW_LOGIN_ACTIVITY_REQUEST = 23231;

    interface Presenter extends PresenterContract<BackupViewModel, ActivityView>
    {
        void createBackup(String sourceType);

        void restoreBackup(String sourceType);

        void doLogin();
    }

    interface ActivityView extends ActivityViewContract<BackupViewModel>, StartForResult {
    }
}
