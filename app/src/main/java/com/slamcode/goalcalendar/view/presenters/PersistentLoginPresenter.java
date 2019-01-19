package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.view.activity.LoginActivityContract;
import com.slamcode.goalcalendar.viewmodels.LoginViewModel;

import java.util.Map;

public class PersistentLoginPresenter implements LoginPresenter {

    private final Map<String, AuthenticationClient> authenticationClients;

    public PersistentLoginPresenter(Map<String, AuthenticationClient> authenticationClients)
    {
        this.authenticationClients = authenticationClients;
    }

    @Override
    public void setData(LoginViewModel data) {

    }

    @Override
    public void initializeWithView(LoginActivityContract.ActivityView activityView) {

    }

    public AuthenticationClient getAuthenticationClient(String name)
    {
        return this.authenticationClients.get(name);
    }

    public Iterable<AuthenticationClient> getAuthenticationClients() {
        return authenticationClients.values();
    }
}
