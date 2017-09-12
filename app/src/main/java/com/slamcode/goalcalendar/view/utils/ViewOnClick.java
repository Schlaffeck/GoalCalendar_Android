package com.slamcode.goalcalendar.view.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ViewOnClick {
    int value();
}
