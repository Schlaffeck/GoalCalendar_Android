package com.slamcode.goalcalendar.authentication.clients.google;

import com.google.android.gms.tasks.Task;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;

public class GoogleAuthenticationClient implements AuthenticationClient {

    public final static String PROVIDER_ID = "google";

    @Override
    public String getAuthenticationProviderId() {
        return null;
    }

    @Override
    public Task<AuthenticationResult> silentSignIn() {
        return null;
    }

    @Override
    public Task<AuthenticationResult> signIn() {
        return null;
    }

    @Override
    public Task<Boolean> signOut() {
        return null;
    }
}
