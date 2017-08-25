package com.slamcode.goalcalendar.view.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by schlaffeck on 20.04.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewReference {
    int value();
}
