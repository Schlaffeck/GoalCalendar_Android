package com.slamcode.goalcalendar.view.activity.base;

import android.app.Activity;
import android.view.View;

public interface ActivityViewContract<ViewModel>
{
    void onDataSet(ViewModel data);

    View getMainView();

    Activity getRelatedActivity();
}
