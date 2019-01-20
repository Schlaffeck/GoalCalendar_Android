package com.slamcode.goalcalendar;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.slamcode.goalcalendar.android.OnActivityResultListener;
import com.slamcode.goalcalendar.android.OnActivityResultProcessor;
import com.slamcode.goalcalendar.android.StartForResult;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.activity.LoginActivityContract;
import com.slamcode.goalcalendar.view.presenters.LoginPresenter;
import com.slamcode.goalcalendar.view.utils.ViewBinder;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.viewmodels.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.slamcode.goalcalendar.view.utils.ViewBinder.findView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginActivityContract.ActivityView, StartForResult, OnActivityResultProcessor {

    private static final String LOG_TAG = "GOAL_LoginAct";

    private List<OnActivityResultListener> onActivityResultListeners = new ArrayList<>();

    @Inject
    LoginPresenter presenter;

    private View mainLayout;
    private LoginViewModel activityViewModel;
    private View mainActivityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.injectDependencies();
        setContentView(R.layout.activity_login);
        this.findRelatedViews();
        this.startMainActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for(OnActivityResultListener listener : this.onActivityResultListeners)
            listener.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void addOnActivityResultListener(OnActivityResultListener listener) {
        this.onActivityResultListeners.add(listener);
    }

    @Override
    public void removeOnActivityResultListener(OnActivityResultListener listener) {
        this.onActivityResultListeners.remove(listener);
    }

    @Override
    public void clearOnActivityResultListeners() {
        this.onActivityResultListeners.clear();
    }

    @Override
    public void onDataSet(LoginViewModel data) {
        if(data == null)
            throw new IllegalArgumentException("Data is null");

        this.activityViewModel = data;
        this.setupDataBindings();
    }

    @Override
    public View getMainView() {
        return this.mainLayout;
    }

    @Override
    public Activity getRelatedActivity() {
        return this;
    }

    private void setupDataBindings() {
        if(this.mainActivityLayout == null)
            this.findRelatedViews();
        ViewDataBinding mainLayoutBinding = DataBindingUtil.bind(this.mainActivityLayout);
        mainLayoutBinding.setVariable(BR.vm, this.activityViewModel);
    }

    private void findRelatedViews()
    {
        this.mainLayout = this.findViewById(android.R.id.content);
        this.mainActivityLayout = findView(this, R.id.login_activity_main_layout);
    }

    private void injectDependencies() {
        ComposableApplication capp = (ComposableApplication)this.getApplication();
        capp.getApplicationComponent().inject(this);
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

    private void onCreateGlobalLayoutAvailable() {
        this.presenter.initializeWithView(this);
    }
}

