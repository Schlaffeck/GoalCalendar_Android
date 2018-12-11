package com.slamcode.goalcalendar.data.inmemory;

import com.slamcode.goalcalendar.data.model.ModelInfoProvider;

public final class DefaultModelInfoProvider implements ModelInfoProvider {

    private final static int MODEL_VERSION = 1;

    @Override
    public int getModelVersion() {
        return MODEL_VERSION;
    }
}
