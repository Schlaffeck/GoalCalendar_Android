package com.slamcode.goalcalendar.view.activity;

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
    }
}
