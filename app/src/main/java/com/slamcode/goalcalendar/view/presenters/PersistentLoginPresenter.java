package com.slamcode.goalcalendar.view.presenters;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;
import com.slamcode.goalcalendar.authentication.impl.AuthenticationTask;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;
import com.slamcode.goalcalendar.view.activity.LoginActivityContract;
import com.slamcode.goalcalendar.viewmodels.LoginProviderViewModel;
import com.slamcode.goalcalendar.viewmodels.LoginViewModel;

import java.util.Map;

public class PersistentLoginPresenter implements LoginPresenter, SourceChangeRequestNotifier.SourceChangeRequestListener<LoginProviderViewModel> {

    private final Map<String, AuthenticationClient> authenticationClients;
    private LoginViewModel data;
    private LoginActivityContract.ActivityView activityView;

    public PersistentLoginPresenter(Map<String, AuthenticationClient> authenticationClients)
    {
        this.authenticationClients = authenticationClients;
    }

    @Override
    public void setData(LoginViewModel data) {
        if(this.data != data) {
            this.data = data;
            this.activityView.onDataSet(data);
        }
    }

    @Override
    public void initializeWithView(LoginActivityContract.ActivityView activityView) {
        this.activityView = activityView;
        if(this.data == null) {
            this.setData(this.provideData());
        }
        else this.resetData();
    }

    public AuthenticationClient getAuthenticationClient(String name)
    {
        return this.authenticationClients.get(name);
    }

    public Iterable<AuthenticationClient> getAuthenticationClients() {
        return authenticationClients.values();
    }

    private void resetData() {
        this.activityView.onDataSet(this.data);
    }

    @Override
    public void doLogin(final String providerId) {
        AuthenticationClient client = this.authenticationClients.get(providerId);
        if(client == null)
            return;

        AuthenticationTask task = client.signIn(this.activityView);
        this.activityView.addOnActivityResultListener(task);
        task.addOnCompleteListener(new OnCompleteListener<AuthenticationResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthenticationResult> task) {
                if(task.isSuccessful())
                    updateFlags(providerId, task.getResult());
            }
        });
    }

    private LoginViewModel provideData()
    {
        final SourceChangeRequestNotifier.SourceChangeRequestListener<LoginProviderViewModel> sourceChangeRequestListener = this;
        return new LoginViewModel(CollectionUtils.select(this.authenticationClients.values(), new ElementSelector<AuthenticationClient, LoginProviderViewModel>() {
            @Override
            public LoginProviderViewModel select(AuthenticationClient parent) {
                final LoginProviderViewModel vm = new LoginProviderViewModel(parent.getAuthenticationProviderId());
                vm.addSourceChangeRequestListener(sourceChangeRequestListener);
                return vm;
            }
        }));
    }

    private LoginProviderViewModel updateFlags(final String providerName, AuthenticationResult authenticationResult)
    {
        LoginProviderViewModel vm = CollectionUtils.singleOrDefault(this.data.getProviders(), new ElementSelector<LoginProviderViewModel, Boolean>() {
            @Override
            public Boolean select(LoginProviderViewModel parent) {
                return parent.getProviderName().equals(providerName);
            }
        });

        if(vm != null) {
            vm.setSignedIn(authenticationResult.isSignedIn());
            vm.setUserId(authenticationResult.getUserId());
        }
        return vm;
    }

    @Override
    public void sourceChangeRequested(LoginProviderViewModel sender, SourceChangeRequestNotifier.SourceChangeRequest request) {
        if(request.getId() == LoginProviderViewModel.DO_LOGIN_REQUEST_ID)
        {
            this.doLogin(sender.getProviderName());
        }
    }
}
