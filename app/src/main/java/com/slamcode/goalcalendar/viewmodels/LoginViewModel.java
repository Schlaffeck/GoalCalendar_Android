package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ObservableList.OnListChangedCallback;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.Predicate;
import com.slamcode.goalcalendar.BR;

import java.util.List;

public class LoginViewModel extends BaseObservable
{
    private ObservableArrayList<LoginProviderViewModel> providers;
    private OnLoginProvidersListChangedCallback onLoginProvidersListChangedCallback;
    private OnLoginProviderViewModelPropertyChanged onLoginProviderViewModelPropertyChanged;

    public LoginViewModel(List<LoginProviderViewModel> providers)
    {
        this.onLoginProvidersListChangedCallback = new OnLoginProvidersListChangedCallback();
        this.onLoginProviderViewModelPropertyChanged = new OnLoginProviderViewModelPropertyChanged();
        this.providers = new ObservableArrayList<>();
        this.providers.addOnListChangedCallback(this.onLoginProvidersListChangedCallback);
        this.providers.addAll(providers);
    }

    public ObservableList<LoginProviderViewModel> getProviders() {
        return providers;
    }

    @Bindable
    public LoginProviderViewModel getSignedInProvider()
    {
        LoginProviderViewModel provider = CollectionUtils.first(this.providers, new Predicate<LoginProviderViewModel>() {
            @Override
            public boolean apply(LoginProviderViewModel item) {
                return item.isSignedIn();
            }
        });

        return provider;
    }


    private class OnLoginProviderViewModelPropertyChanged extends OnPropertyChangedCallback {

        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if(propertyId == BR.signedIn)
                notifyPropertyChanged(BR.signedInProvider);
        }
    }

    private class OnLoginProvidersListChangedCallback extends OnListChangedCallback<ObservableArrayList<LoginProviderViewModel>>
    {

        @Override
        public void onChanged(ObservableArrayList<LoginProviderViewModel> sender) {

        }

        @Override
        public void onItemRangeChanged(ObservableArrayList<LoginProviderViewModel> sender, int positionStart, int itemCount) {

        }

        @Override
        public void onItemRangeInserted(ObservableArrayList<LoginProviderViewModel> sender, int positionStart, int itemCount) {
            for(int i = positionStart; i < positionStart + itemCount; i++)
            {
                sender.get(i).addOnPropertyChangedCallback(onLoginProviderViewModelPropertyChanged);
            }
        }

        @Override
        public void onItemRangeMoved(ObservableArrayList<LoginProviderViewModel> sender, int fromPosition, int toPosition, int itemCount) {

        }

        @Override
        public void onItemRangeRemoved(ObservableArrayList<LoginProviderViewModel> sender, int positionStart, int itemCount) {

        }
    }
}
