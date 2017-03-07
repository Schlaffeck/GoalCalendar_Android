package com.slamcode.goalcalendar;

import android.app.DialogFragment;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;

import com.slamcode.goalcalendar.view.activity.MonthlyGoalsActivityContract;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.service.commands.AutoMarkTasksCommand;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;
import com.slamcode.goalcalendar.view.presenters.PresentersSource;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MonthlyGoalsActivity extends AppCompatActivity implements MonthlyGoalsActivityContract.ActivityView{

    final String ACTIVITY_ID = MonthlyGoalsActivity.class.getName();

    @BindView(R.id.monthly_goals_activity_main_coordinator_layout)
    CoordinatorLayout monthlyGoalsActivityLayout;

    @BindView(R.id.monthly_goals_listview)
    RecyclerView monthlyGoalsListView;

    @BindView(R.id.monthly_goals_emptyContent_horizontallScrollView)
    HorizontalScrollView emptyContentHorizontalScrollView;

    // dependencies
    @Inject
    ActivityViewStateProvider viewStateProvider;

    @Inject
    PersistenceContext persistenceContext;

    @Inject
    ItemsCollectionAdapterProvider adapterProvider;
    
    @Inject
    AutoMarkTasksCommand autoMarkTasksCommand;

    @Inject
    PresentersSource presentersSource;

    private GestureDetectorCompat gestureDetector;

    private BottomSheetBehavior bottomSheetBehavior;

    private MonthlyGoalsActivityContract.Presenter presenter;
    private ViewDataBinding mainActivityContentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.slamcode.goalcalendar.R.layout.monthly_goals_activity);

        this.injectDependencies();
        ButterKnife.bind(this);

        this.setSupportActionBar((Toolbar)this.findViewById(R.id.toolbar));
        this.setupSwipeListener();
        this.runStartupCommands();
    }

    private void setupBottomSheetBehavior() {
        NestedScrollView bottomSheetScrollView = (NestedScrollView) this.findViewById(R.id.monthly_goals_activity_bottom_sheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetScrollView);
        this.bottomSheetBehavior.setPeekHeight(150);
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void runStartupCommands() {
        this.autoMarkTasksCommand.execute(this.findViewById(R.id.monthly_goals_activity_main_coordinator_layout));
    }

    private void injectDependencies() {
        ComposableApplication capp = (ComposableApplication)this.getApplication();
        capp.getApplicationComponent().inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.slamcode.goalcalendar.R.menu.menu_monthly_goals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == com.slamcode.goalcalendar.R.id.action_settings) {
            Intent goToSettings = new Intent(this, SettingsActivity.class);
            this.startActivity(goToSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(this.persistenceContext != null)
            this.persistenceContext.persistData();
        super.onStop();
    }

    private void setupSwipeListener()
    {
        this.gestureDetector = new GestureDetectorCompat(this, new HorizontalFlingGestureListener());
        this.monthlyGoalsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        this.emptyContentHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public void onDataSet(MonthlyGoalsViewModel data) {
        if(this.mainActivityContentBinding == null)
            this.mainActivityContentBinding =  DataBindingUtil.bind(this.monthlyGoalsActivityLayout);

        this.mainActivityContentBinding.setVariable(BR.presenter, this.presenter);
        this.mainActivityContentBinding.setVariable(BR.vm, data);
    }

    @Override
    public void showDialog(DialogFragment dialogFragment) {
        dialogFragment.show(this.getFragmentManager(), null);
    }

    private class HorizontalFlingGestureListener extends GestureDetector.SimpleOnGestureListener{

        private static final String LOG_TAG = "GOAL_GestDet";

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(LOG_TAG, "onFling: " + e1 +"; "+ e2.toString());
            return this.checkOnSwipe(e1, e2, velocityX, velocityY);
        }

        public boolean checkOnSwipe(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH){
                    return false;
                }
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onLeftSwipe();
                }
                // left to right swipe
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onRightSwipe();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private void onRightSwipe() {
            Log.d(LOG_TAG, "Right swipe");
            presenter.goToPreviousMonth(null);
        }

        private void onLeftSwipe() {
            Log.d(LOG_TAG, "Left swipe");
            presenter.goToNextMonth(null);
        }
    }
}
