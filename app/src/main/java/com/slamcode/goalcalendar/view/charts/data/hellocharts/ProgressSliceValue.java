package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import lecho.lib.hellocharts.model.SliceValue;

/**
 * Class extending slice value for pie charts where there is a possibility to
 * draw set progress value within one slice drawn.
 */

public class ProgressSliceValue extends SliceValue {

    private float thresholdValue;

    public float getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(float thresholdValue) {
        this.thresholdValue = thresholdValue;
    }
}
