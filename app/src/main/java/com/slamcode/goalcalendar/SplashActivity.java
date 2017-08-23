package com.slamcode.goalcalendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    private final static String LOG_TAG = "GC_SplashAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Splash activity creating - START");
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MonthlyGoalsActivity.class);
        Log.d(LOG_TAG, "Launching Monthly Goals Activity");
        startActivity(intent);
        finish();
        Log.d(LOG_TAG, "Splash activity finished");
        Log.d(LOG_TAG, "Splash activity creating - END");
    }
}
