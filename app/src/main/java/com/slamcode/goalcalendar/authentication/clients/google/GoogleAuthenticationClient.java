package com.slamcode.goalcalendar.authentication.clients.google;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.android.StartForResult;
import com.slamcode.goalcalendar.android.tasks.TaskAbstract;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationToken;
import com.slamcode.goalcalendar.authentication.impl.AuthenticationTask;
import com.slamcode.goalcalendar.authentication.impl.DefaultAuthenticationResult;

public class GoogleAuthenticationClient implements AuthenticationClient {

    public final static String PROVIDER_ID = "google";
    private static final int GOOGLE_SIGN_IN_REQUEST = 23233;
    private static final String LOG_TAG = "GOAL_googleAuthCli";

    private final ApplicationContext applicationContext;

    private GoogleSignInClient googleSignInClient = null;
    private GoogleSignInAccount googleSignInAccount = null;

    public GoogleAuthenticationClient(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        this.setup();
    }

    @Override
    public String getAuthenticationProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public AuthenticationResult currentSignInData() {
        if(this.googleSignInAccount != null)
        {
            new DefaultAuthenticationResult(PROVIDER_ID, googleSignInAccount.getId(), new AuthenticationToken(googleSignInAccount.getIdToken()));
        }

        return new DefaultAuthenticationResult(PROVIDER_ID);
    }

    @NonNull
    @Override
    public AuthenticationTask silentSignIn() {
        GoogleAuthenticationTask task = new GoogleAuthenticationTask(this.applicationContext, this.googleSignInClient, true);
        task.start();
        return task;
    }

    @NonNull
    @Override
    public AuthenticationTask signIn(StartForResult startForResult) {
        GoogleAuthenticationTask task = new GoogleAuthenticationTask(this.applicationContext, this.googleSignInClient, false);
        task.start(startForResult);
        return task;
    }

    @NonNull
    @Override
    public Task<Boolean> signOut() {
        return this.googleSignInClient.signOut().continueWithTask(new Continuation<Void, Task<Boolean>>() {
            @Override
            public Task<Boolean> then(@NonNull final Task<Void> task) {
                return new TaskAbstract<Boolean>()
                {
                    @Override
                    protected void start() {
                        if(task.isSuccessful())
                            this.setSuccessStatus(true);
                        else if(task.isCanceled())
                            this.setCanceled();
                        else
                            this.setFailureStatus(task.getException());
                    }
                };
            };
        });
    }

    private void setup()
    {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(this.applicationContext.getStringFromResources(R.string.goal_calendar_web_client_id))
                .build();

        this.googleSignInClient = GoogleSignIn.getClient(this.applicationContext.getDefaultContext(), options);
        this.googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this.applicationContext.getDefaultContext());
    }

    private class GoogleAuthenticationTask extends com.slamcode.goalcalendar.authentication.impl.AuthenticationTask implements OnCompleteListener<GoogleSignInAccount> {
        private final ApplicationContext applicationContext;
        private final GoogleSignInClient googleSignInClient;
        private final boolean silent;
        private Intent startedIntent;

        GoogleAuthenticationTask(ApplicationContext applicationContext, GoogleSignInClient googleSignInClient, boolean silent) {
            this.applicationContext = applicationContext;
            this.googleSignInClient = googleSignInClient;
            this.silent = silent;
        }

        @Override
        protected void start()
        {
            if(this.silent)
                this.googleSignInClient.silentSignIn().addOnCompleteListener(this);
            this.startedIntent = this.googleSignInClient.getSignInIntent();
            this.applicationContext.getDefaultContext().startActivity(this.startedIntent);
        }

        @Override
        protected void start(StartForResult startForResult) {
            this.startedIntent = this.googleSignInClient.getSignInIntent();
            startForResult.startActivityForResult(this.startedIntent, GOOGLE_SIGN_IN_REQUEST);
        }

        @Override
        public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (requestCode == GOOGLE_SIGN_IN_REQUEST ||
                    data.equals(this.startedIntent)) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                return true;
            }
            return false;
        }

        private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
            try {
                googleSignInAccount = completedTask.getResult(ApiException.class);
                if(googleSignInAccount != null)
                    this.setSuccessStatus(new DefaultAuthenticationResult(PROVIDER_ID, googleSignInAccount.getId(), new AuthenticationToken(googleSignInAccount.getIdToken())));
                else
                    this.setSuccessStatus(new DefaultAuthenticationResult(PROVIDER_ID));
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(LOG_TAG, "signInResult:failed code=" + e.getStatusCode());
                this.setFailureStatus(e);
            }
        }

        @Override
        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
            this.handleSignInResult(task);
        }
    }
}
