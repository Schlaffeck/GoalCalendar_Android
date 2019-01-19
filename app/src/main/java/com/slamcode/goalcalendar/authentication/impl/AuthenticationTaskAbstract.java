package com.slamcode.goalcalendar.authentication.impl;

import com.slamcode.goalcalendar.android.OnActivityResultListener;
import com.slamcode.goalcalendar.android.StartForResult;
import com.slamcode.goalcalendar.android.tasks.TaskAbstract;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;

public abstract class AuthenticationTaskAbstract extends TaskAbstract<AuthenticationResult> implements OnActivityResultListener {

    protected abstract void start(StartForResult startForResult);
}
