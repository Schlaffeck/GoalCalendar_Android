package com.slamcode.goalcalendar.service;

import android.content.Intent;

/**
 * Interface for services started at application run start
 */

public interface ApplicationStartupService {

    void serviceStartup(Intent intent);
}
