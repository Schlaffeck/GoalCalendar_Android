package com.slamcode.goalcalendar.view.validation.base;

import android.view.View;

/**
 * Created by moriasla on 22.12.2016.
 */

public interface ViewValidator<ViewType extends View> {

    boolean isValid();

    boolean validate(ViewType view);
}
