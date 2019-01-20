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
import com.slamcode.goalcalendar.view.activity.LoginActivityContract;
import com.slamcode.goalcalendar.view.presenters.LoginPresenter;
import com.slamcode.goalcalendar.view.utils.ViewBinder;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.viewmodels.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener, LoginActivityContract.ActivityView, StartForResult, OnActivityResultProcessor {

    private static final int GOOGLE_SIGN_IN_REQUEST = 1221;

    private static final String LOG_TAG = "GOAL_LoginAct";

    // google sign in
    private GoogleSignInClient googleSignInClient = null;
    private GoogleSignInAccount googleSignInAccount = null;

    private List<OnActivityResultListener> onActivityResultListeners = new ArrayList<>();

    @Inject
    LoginPresenter presenter;

    @ViewReference(R.id.login_google_sign_in_button)
    private SignInButton googleSignInButton;
    private View mainLayout;
    private LoginViewModel activityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.injectDependencies();
        setContentView(R.layout.activity_login);
        this.findRelatedViews();
        this.setupGoogleSignInClient();
    }

    private void setupGoogleSignInClient()
    {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        this.googleSignInClient = GoogleSignIn.getClient(this, options);
        this.googleSignInButton = ViewBinder.findView(this, R.id.login_google_sign_in_button);
        this.googleSignInButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.checkLastGoogleSignInAccount();
    }

    private void checkLastGoogleSignInAccount() {

        this.googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(this.googleSignInAccount != null)
            this.updateUi(this.googleSignInAccount);
    }

    private void updateUi(GoogleSignInAccount googleSignInAccount) {
        // hide login button - return to previous activity
        if(googleSignInAccount != null)
            this.googleSignInButton.setVisibility(View.GONE);
        this.finish();
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            this.googleSignInAccount = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            this.updateUi(this.googleSignInAccount);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(LOG_TAG, "signInResult:failed code=" + e.getStatusCode());
            this.updateUi(null);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.login_google_sign_in_button:
                this.signInWithGoogle();
        }
    }

    private void signInWithGoogle() {
        Intent intent = this.googleSignInClient.getSignInIntent();
        startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST);
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
        ViewDataBinding mainLayoutBinding = DataBindingUtil.bind(this.mainLayout);
        mainLayoutBinding.setVariable(BR.vm, this.activityViewModel);
    }

    private void findRelatedViews()
    {
        this.mainLayout = this.findViewById(android.R.id.content);
        this.googleSignInButton = this.findViewById(R.id.login_google_sign_in_button);
    }

    private void injectDependencies() {
        ComposableApplication capp = (ComposableApplication)this.getApplication();
        capp.getApplicationComponent().inject(this);
    }
}

