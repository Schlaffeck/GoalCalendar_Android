package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.List;

public class LoginViewModel extends BaseObservable {

    private ObservableArrayList<LoginProviderViewModel> providers;

    public LoginViewModel(List<LoginProviderViewModel> providers)
    {
        this.providers = new ObservableArrayList<>();
        this.providers.addAll(providers);
    }

    public ObservableList<LoginProviderViewModel> getProviders() {
        return providers;
    }
}
