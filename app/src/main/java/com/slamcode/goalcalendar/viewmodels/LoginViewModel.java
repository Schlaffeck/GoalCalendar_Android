package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

public class LoginViewModel extends BaseObservable {

    private List<LoginProviderViewModel> providers;

    @Bindable
    public List<LoginProviderViewModel> getProviders() {
        return providers;
    }

    public void setProviders(List<LoginProviderViewModel> providers) {
        this.providers = providers;
    }
}
