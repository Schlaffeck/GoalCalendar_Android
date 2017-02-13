package com.slamcode.goalcalendar.diagniostics;

import android.util.Log;

/**
 * Created by moriasla on 13.02.2017.
 */

public class SystemLogger implements Logger {
    @Override
    public void d(String logTag, String message) {
        Log.d(logTag, message);
    }

    @Override
    public void v(String logTag, String message) {
        Log.d(logTag, message);
    }
}
