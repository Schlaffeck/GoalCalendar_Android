package com.slamcode.goalcalendar.view.activity;

/**
 * Created by moriasla on 13.01.2017.
 */

public class ActivityViewState {

    private String activityId;

    private boolean wasDisplayed;

    public ActivityViewState(String activityId)
    {
        this.activityId = activityId;
    }

    public boolean isWasDisplayed() {
        return wasDisplayed;
    }

    public void setWasDisplayed(boolean wasDisplayed) {
        this.wasDisplayed = wasDisplayed;
    }
}
