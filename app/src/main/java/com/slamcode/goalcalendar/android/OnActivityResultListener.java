package com.slamcode.goalcalendar.android;

import android.content.Intent;

public interface OnActivityResultListener {

    boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
