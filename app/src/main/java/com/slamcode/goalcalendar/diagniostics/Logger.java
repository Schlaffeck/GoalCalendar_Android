package com.slamcode.goalcalendar.diagniostics;

/**
 * Own logger itnerface to mock logging functionality in porject
 */

public interface Logger {

    void d(String logTag, String message);

    void v(String logTag, String message);

    void e(String logTag, String message);
}
