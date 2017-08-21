package com.slamcode.goalcalendar.view;

public interface ViewProcessingState {

    boolean isProcessingView();

    void startProcessingView(String viewId);

    void stopProcessingView(String viewId);

    void addChangeListener(ChangeListener listener);

    void removeChangeListener(ChangeListener listener);

    void clearChangeListeners();

    interface ChangeListener{

        void onStartedProcessingViews();

        void onStoppedProcessingAllViews();
    }
}
