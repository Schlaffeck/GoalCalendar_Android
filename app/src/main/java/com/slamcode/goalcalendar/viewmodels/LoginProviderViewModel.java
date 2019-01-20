package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;

import java.util.ArrayList;

public class LoginProviderViewModel extends BaseObservable implements SourceChangeRequestNotifier<LoginProviderViewModel> {

    public final static int DO_LOGIN_REQUEST_ID = 921823;

    private final String providerName;
    private boolean signedIn;
    private String userId;

    private ArrayList<SourceChangeRequestListener<LoginProviderViewModel>> sourceChangeRequestListeners;

    public LoginProviderViewModel(String providerName)
    {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }

    @Bindable
    public boolean isSignedIn() {
        return signedIn;
    }

    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    @Bindable
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public void addSourceChangeRequestListener(SourceChangeRequestListener<LoginProviderViewModel> listener) {
        if(listener != null
                && !this.sourceChangeRequestListeners.contains(listener))
            this.sourceChangeRequestListeners.add(listener);
    }

    @Override
    public void removeSourceChangeRequestListener(SourceChangeRequestListener<LoginProviderViewModel> listener) {
        this.sourceChangeRequestListeners.remove(listener);
    }

    @Override
    public void clearSourceChangeRequestListeners() {
        this.sourceChangeRequestListeners.clear();
    }


    @Override
    public void notifySourceChangeRequested(SourceChangeRequest request)
    {
        for (SourceChangeRequestListener<LoginProviderViewModel> listener : this.sourceChangeRequestListeners)
        {
            listener.sourceChangeRequested(this, request);
        }
    }
}
