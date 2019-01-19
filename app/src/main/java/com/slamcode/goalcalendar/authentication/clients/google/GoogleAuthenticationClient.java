package com.slamcode.goalcalendar.authentication.clients.google;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;
import com.slamcode.goalcalendar.authentication.impl.AuthenticationTaskAbstract;

public class GoogleAuthenticationClient implements AuthenticationClient {

    public final static String PROVIDER_ID = "google";
    private static final int GOOGLE_SIGN_IN_REQUEST = 2389238;
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
    public Task<AuthenticationResult> silentSignIn() {
        return null;
    }

    @Override
    public Task<AuthenticationResult> signIn() {
        return new AuthenticationTask(this.applicationContext, this.googleSignInClient);
    }

    @Override
    public Task<Boolean> signOut() {
        return null;
    }

    private void setup()
    {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        this.googleSignInClient = GoogleSignIn.getClient(this.applicationContext.getDefaultContext(), options);
        this.googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this.applicationContext.getDefaultContext());
    }

    private class AuthenticationTask extends AuthenticationTaskAbstract implements PreferenceManager.OnActivityResultListener
    {
        private final ApplicationContext applicationContext;
        private final GoogleSignInClient googleSignInClient;

        AuthenticationTask(ApplicationContext applicationContext, GoogleSignInClient googleSignInClient) {
            this.applicationContext = applicationContext;
            this.googleSignInClient = googleSignInClient;
        }

        @Override
        protected void start()
        {
            this.applicationContext.getDefaultContext().startActivity(this.googleSignInClient.getSignInIntent());

        }

        @Override
        public boolean onActivityResult(int requestCode, int resultCode, Intent data) {

            // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
            if (requestCode == GOOGLE_SIGN_IN_REQUEST) {
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
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.w(LOG_TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }
}
