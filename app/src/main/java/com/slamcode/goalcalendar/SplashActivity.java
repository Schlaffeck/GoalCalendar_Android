package com.slamcode.goalcalendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    private final static String LOG_TAG = "GOAL_SplashAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Splash activity creating - START");
        super.onCreate(savedInstanceState);
        if(!this.getIntent().getBooleanExtra(MonthlyGoalsActivity.STARTED_FROM_PARENT_INTENT_PARAM, false)) {
            Intent newIntent = new Intent(this, MonthlyGoalsActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
        }
            finish();
        Log.d(LOG_TAG, "Splash activity finished");
        Log.d(LOG_TAG, "Splash activity creating - END");
    }
}
