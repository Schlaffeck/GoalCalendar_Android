package com.slamcode.goalcalendar.authentication;

import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;

public interface AuthenticationProvider {

    Iterable<AuthenticationClient> getAllClients();

    AuthenticationClient getClient(String clientId);

    AuthenticationResult getCurrentAuthenticationData();
}
