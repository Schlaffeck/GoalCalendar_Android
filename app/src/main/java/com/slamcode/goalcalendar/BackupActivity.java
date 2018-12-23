package com.slamcode.goalcalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.view.activity.BackupActivityContract;
import com.slamcode.goalcalendar.viewmodels.BackupViewModel;

import javax.inject.Inject;

public class BackupActivity extends AppCompatActivity
    implements BackupActivityContract.ActivityView
{
    final String ACTIVITY_ID = BackupActivity.class.getName();

    private final static String LOG_TAG = "GOAL_BackupAct";

    @Inject
    BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry;

    private BackupViewModel activityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.injectDependencies();
        setContentView(R.layout.activity_backup);
    }

    @Override
    public void onDataSet(BackupViewModel data) {
        if(data == null)
            throw new IllegalArgumentException("Data is null");

        this.activityViewModel = data;

        this.setupDataBindings();
    }

    private void setupDataBindings() {
        // TODO; setup bindings to views
    }

    private void injectDependencies() {
        ComposableApplication capp = (ComposableApplication)this.getApplication();
        capp.getApplicationComponent().inject(this);
    }
}
