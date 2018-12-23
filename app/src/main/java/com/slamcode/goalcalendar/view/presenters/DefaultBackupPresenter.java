package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.view.activity.BackupActivityContract;
import com.slamcode.goalcalendar.viewmodels.BackupViewModel;

public class DefaultBackupPresenter implements BackupPresenter {

    private BackupViewModel data;
    private BackupActivityContract.ActivityView activityView;

    private PersistenceContext persistenceContext;

    public DefaultBackupPresenter(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    @Override
    public void setData(BackupViewModel data) {
        if(this.data != data) {
            this.data = data;
            this.activityView.onDataSet(data);
        }
    }

    @Override
    public void initializeWithView(BackupActivityContract.ActivityView activityView) {
        this.activityView = activityView;
        if(this.data == null)
            this.setData(new BackupViewModel());
        else this.resetData();
    }

    @Override
    public void createBackup(String sourceType) {
        return;
    }

    private void resetData() {
        this.activityView.onDataSet(this.data);
    }
}
