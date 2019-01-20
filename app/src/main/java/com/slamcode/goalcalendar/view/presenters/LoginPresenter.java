package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.view.activity.LoginActivityContract;

public interface LoginPresenter extends LoginActivityContract.Presenter {

    void doLogin(String providerID);
}
