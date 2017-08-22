package com.slamcode.goalcalendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!this.getIntent().getBooleanExtra(MonthlyGoalsActivity.STARTED_FROM_PARENT_INTENT_PARAM, false)) {
            Intent newIntent = new Intent(this, MonthlyGoalsActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
        }
            finish();
    }
}
