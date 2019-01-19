package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

public class LoginProviderViewModel extends BaseObservable {

    private final String providerName;

    public LoginProviderViewModel(String providerName)
    {
        this.providerName = providerName;
    }

    @Bindable
    public String getProviderName() {
        return providerName;
    }
}
