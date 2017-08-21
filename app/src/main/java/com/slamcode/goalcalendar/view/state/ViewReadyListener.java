package com.slamcode.goalcalendar.view.state;

import android.view.View;

/**
 * Interface for listener notifying about view getting loaded and fully rendered
 */

public interface ViewReadyListener {

    void onViewReady(View view);
}
