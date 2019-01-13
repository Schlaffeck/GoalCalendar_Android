package com.slamcode.goalcalendar.view.activity;

import android.app.Activity;
import android.view.View;

import com.slamcode.goalcalendar.viewmodels.BackupViewModel;

public interface BackupActivityContract {

    int SHOW_LOGIN_ACTIVITY_REQUEST = 23231;

    interface Presenter{

        void setData(BackupViewModel data);

        void initializeWithView(ActivityView activityView);

        void createBackup(String sourceType);

        void restoreBackup(String sourceType);

        void doLogin();
    }

    interface ActivityView
    {
        void onDataSet(BackupViewModel data);

        View getMainView();

        Activity getRelatedActivity();
    }
}
