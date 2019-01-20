package com.slamcode.goalcalendar.view.lists;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.BaseSourceChangeRequest;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.viewmodels.BackupSourceViewModel;
import com.slamcode.goalcalendar.viewmodels.LoginProviderViewModel;
import com.slamcode.goalcalendar.viewmodels.LoginViewModel;

public class LoginProvidersRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<LoginProviderViewModel, LoginProvidersRecyclerViewAdapter.ViewHolder> {

    LoginProvidersRecyclerViewAdapter()
    {
        super(new ObservableArrayList<LoginProviderViewModel>());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_login_provider_layout, null);
        return new LoginProvidersRecyclerViewAdapter.ViewHolder(view);
    }

    public class ViewHolder extends BindableViewHolderBase<LoginProviderViewModel>{

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindToModel(LoginProviderViewModel modelObject) {
            super.bindToModel(modelObject);
            this.getBinding().setVariable(BR.presenter, this);
        }

        public void requestSignIn()
        {
            this.getModelObject().notifySourceChangeRequested(new BaseSourceChangeRequest(LoginProviderViewModel.DO_LOGIN_REQUEST_ID));
        }
    }
}
