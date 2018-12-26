package com.slamcode.goalcalendar;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.view.activity.BackupActivityContract;
import com.slamcode.goalcalendar.view.presenters.PresentersSource;
import com.slamcode.goalcalendar.view.utils.ViewBinder;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.viewmodels.BackupViewModel;

import javax.inject.Inject;

import static com.slamcode.goalcalendar.view.utils.ViewBinder.*;

public class BackupActivity extends AppCompatActivity
    implements BackupActivityContract.ActivityView
{
    final String ACTIVITY_ID = BackupActivity.class.getName();

    private final static String LOG_TAG = "GOAL_BackupAct";

    private boolean dataBindingsSet;

    @Inject
    BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry;

    @Inject
    PresentersSource presentersSource;

    @ViewReference(R.id.backup_activity_mainLayout)
    ConstraintLayout mainActivityLayout;

    private BackupViewModel activityViewModel;
    private BackupActivityContract.Presenter presenter;

    private View mainLayout;
    private ViewDataBinding mainLayoutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.injectDependencies();
        setContentView(R.layout.activity_backup);
        this.findAllRelatedViews();
        this.startMainActivity();
    }

    @Override
    public void onDataSet(BackupViewModel data) {
        if(data == null)
            throw new IllegalArgumentException("Data is null");

        this.activityViewModel = data;
        this.dataBindingsSet = false;

        this.setupDataBindings();
    }

    @Override
    public View getMainView() {
        return this.mainLayout;
    }

    private void startMainActivity()
    {
        this.mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                View v = mainLayout;
                v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                onCreateGlobalLayoutAvailable();
                Log.d(LOG_TAG, "Backup activity view rendered");
            }
        });
    }

    private void setupDataBindings() {
        if(this.dataBindingsSet
                || this.activityViewModel == null)
            return;

        if(this.mainActivityLayout == null)
        {
            this.findAllRelatedViews();
            if(this.mainActivityLayout == null)
                throw new IllegalArgumentException("backup activity layout is null and can not be found in activity view");
        }

        if(this.mainLayoutBinding == null)
            this.mainLayoutBinding = DataBindingUtil.bind(this.mainActivityLayout);
        this.mainLayoutBinding.setVariable(BR.vm, this.activityViewModel);
        this.mainLayoutBinding.setVariable(BR.presenter, this.presenter);
    }

    private void injectDependencies() {
        ComposableApplication capp = (ComposableApplication)this.getApplication();
        capp.getApplicationComponent().inject(this);
    }

    private void findAllRelatedViews()
    {
        this.mainLayout = this.findViewById(android.R.id.content);
        this.mainActivityLayout = findView(this, R.id.backup_activity_mainLayout);
    }

    private void setupPresenter() {
        this.presenter = this.presentersSource.getBackupPresenter(this);
        this.presenter.initializeWithView(this);
    }

    private void onCreateGlobalLayoutAvailable()
    {
        this.setSupportActionBar((Toolbar)this.findViewById(R.id.toolbar));
        this.setupPresenter();
    }
}
