package com.slamcode.goalcalendar.authentication.clients;

public interface AuthenticationResult {

    String getUserId();

    String getAuthenticationProviderId();

    boolean isSignedIn();

    boolean isOauthAvailable();

    AuthenticationToken getOAuthToken();
}
