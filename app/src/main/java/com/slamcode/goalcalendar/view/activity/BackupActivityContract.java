package com.slamcode.goalcalendar.view.activity;

import android.view.View;

import com.slamcode.goalcalendar.viewmodels.BackupViewModel;

public interface BackupActivityContract {

    interface Presenter{

        void setData(BackupViewModel data);

        void initializeWithView(ActivityView activityView);

        void createBackup(String sourceType);
    }

    interface ActivityView
    {
        void onDataSet(BackupViewModel data);

        View getMainView();
    }
}
