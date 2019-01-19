package com.slamcode.goalcalendar.android;

public interface OnActivityResultProcessor {

    void addOnActivityResultListener(OnActivityResultListener listener);

    void removeOnActivityResultListener(OnActivityResultListener listener);

    void clearOnActivityResultListeners();
}
