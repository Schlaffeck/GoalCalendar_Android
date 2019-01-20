package com.slamcode.goalcalendar.authentication.clients;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;

public interface AuthenticationClient {

    String getAuthenticationProviderId();

    @NonNull
    Task<AuthenticationResult> silentSignIn();

    @NonNull
    Task<AuthenticationResult> signIn();

    @NonNull
    Task<Boolean> signOut();
}
