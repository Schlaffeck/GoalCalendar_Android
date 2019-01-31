package com.slamcode.goalcalendar.authentication.clients;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.slamcode.goalcalendar.android.StartForResult;
import com.slamcode.goalcalendar.authentication.impl.AuthenticationTask;

public interface AuthenticationClient {

    String getAuthenticationProviderId();

    @NonNull
    AuthenticationResult currentSignInData();

    @NonNull
    AuthenticationTask silentSignIn();

    @NonNull
    AuthenticationTask signIn(StartForResult startForResult);

    @NonNull
    Task<Boolean> signOut();
}
