package com.slamcode.goalcalendar.authentication.clients;

import com.google.android.gms.tasks.Task;

public interface AuthenticationClient {

    String getAuthenticationProviderId();

    Task<AuthenticationResult> silentSignIn();

    Task<AuthenticationResult> signIn();

    Task<Boolean> signOut();
}
