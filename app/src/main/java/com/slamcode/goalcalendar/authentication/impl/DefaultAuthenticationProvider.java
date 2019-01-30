package com.slamcode.goalcalendar.authentication.impl;

import com.slamcode.goalcalendar.authentication.AuthenticationProvider;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationToken;

import java.util.Map;

public class DefaultAuthenticationProvider implements AuthenticationProvider {

    private final Map<String, AuthenticationClient> clientMap;

    private AuthenticationResult currentAuthenticationData = new NotAuthenticatedResult();

    public DefaultAuthenticationProvider(Map<String, AuthenticationClient> clientMap)
    {
        this.clientMap = clientMap;
    }

    @Override
    public Iterable<AuthenticationClient> getAllClients() {
        return this.clientMap.values();
    }

    @Override
    public AuthenticationClient getClient(String clientId) {
        return this.clientMap.get(clientId);
    }

    @Override
    public AuthenticationResult getCurrentAuthenticationData() {
        this.checkIfAlreadySignedIn();
        return this.currentAuthenticationData;
    }

    private void checkIfAlreadySignedIn()
    {
        if(this.currentAuthenticationData != null && this.currentAuthenticationData.isSignedIn())
            return;

        boolean signedIn = false;
        for (AuthenticationClient client :
                this.clientMap.values()) {
            if(client.currentSignInData().isSignedIn())
            {
                this.currentAuthenticationData = client.currentSignInData();
                signedIn = true;
                break;
            }
        }

        if(!signedIn)
            this.currentAuthenticationData = new NotAuthenticatedResult();
    }

    private class NotAuthenticatedResult implements AuthenticationResult
    {
        @Override
        public String getUserId() {
            return null;
        }

        @Override
        public String getAuthenticationProviderId() {
            return null;
        }

        @Override
        public boolean isSignedIn() {
            return false;
        }

        @Override
        public boolean isOauthAvailable() {
            return false;
        }

        @Override
        public AuthenticationToken getOAuthToken() {
            return null;
        }
    }
}
