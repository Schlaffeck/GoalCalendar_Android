package com.slamcode.goalcalendar.authentication.impl;

import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationToken;

public class DefaultAuthenticationResult implements AuthenticationResult {

    private final String providerId;
    private final String userId;
    private final AuthenticationToken oauthToken;

    public DefaultAuthenticationResult(String providerId, String userId, AuthenticationToken oauthToken)
    {
        this.providerId = providerId;
        this.userId = userId;
        this.oauthToken = oauthToken;
    }

    public DefaultAuthenticationResult(String providerId)
    {
        this.providerId = providerId;
        this.userId = null;
        this.oauthToken = null;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getAuthenticationProviderId() {
        return this.providerId;
    }

    @Override
    public boolean isSignedIn() {
        return this.userId != null;
    }

    @Override
    public boolean isOauthAvailable() {
        return this.oauthToken != null;
    }

    @Override
    public AuthenticationToken getOAuthToken() {
        return oauthToken;
    }
}
