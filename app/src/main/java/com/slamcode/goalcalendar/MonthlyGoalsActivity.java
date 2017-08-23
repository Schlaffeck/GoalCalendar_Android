package com.slamcode.goalcalendar;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangedService;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryDescriptionProvider;
import com.slamcode.goalcalendar.settings.AppSettingsManager;
import com.slamcode.goalcalendar.view.activity.MonthlyGoalsActivityContract;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.service.commands.AutoMarkTasksCommand;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.dialogs.DailyProgressDialog;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;
import com.slamcode.goalcalendar.view.lists.utils.ScrollableViewHelper;
import com.slamcode.goalcalendar.view.presenters.PresentersSource;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.view.utils.ViewBinder;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

import javax.inject.Inject;

public class MonthlyGoalsActivity extends AppCompatActivity implements MonthlyGoalsActivityContract.ActivityView{

    final String ACTIVITY_ID = MonthlyGoalsActivity.class.getName();

    private final static String LOG_TAG = "GC_MGoalsAct";

    View mainLayout;

    @ViewReference(R.id.monthly_goals_activity_main_coordinator_layout)
    CoordinatorLayout monthlyGoalsActivityLayout;

    @ViewReference(R.id.monthly_goals_listview)
    RecyclerView categoryNamesRecyclerView;

    @ViewReference(R.id.monthly_goals_dailyplans_listview)
    RecyclerView categoryPlansRecyclerView;

    @ViewReference(R.id.content_monthly_goals)
    RelativeLayout monthlyPlansGridContentLayout;

    @ViewReference(R.id.monthly_goals_emptyContent_horizontallScrollView)
    HorizontalScrollView emptyContentHorizontalScrollView;

    @ViewReference(R.id.monthly_goals_summary_content_layout)
    LinearLayout summaryContentLayout;

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

    @Inject
    DateTimeChangedService dateTimeChangedService;

    @Inject
    AppSettingsManager settingsManager;

    @Inject
    PlansSummaryDescriptionProvider descriptionProvider;

    private BottomSheetBehavior bottomSheetBehavior;

    private MonthlyGoalsActivityContract.Presenter presenter;

    private ViewDataBinding mainActivityContentBinding;

    private MonthlyGoalsViewModel activityViewModel;

    private NestedScrollView bottomSheetScrollView;

    private boolean exitApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Monthly goals activity creating - START");
        super.onCreate(savedInstanceState);
        setContentView(com.slamcode.goalcalendar.R.layout.monthly_goals_activity);
        Log.d(LOG_TAG, "Monthly goals activity view set");

        this.mainLayout = this.findViewById(android.R.id.content);
        this.injectDependencies();
        Log.d(LOG_TAG, "Monthly goals dependencies injected");

        this.mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                View v = mainLayout;
                v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                onCreateGlobalLayoutAvailable();
                Log.d(LOG_TAG, "Monthly goals activity view rendered");
            }
        });
        Log.d(LOG_TAG, "Monthly goals activity creating - END");
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
    protected void onStart() {
        super.onStart();
        this.showDailyProgressDialog();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        this.registerReceiver(this.dateTimeChangedService, intentFilter);
    }

    @Override
    protected void onStop() {
        if(this.persistenceContext != null)
            this.persistenceContext.persistData();
        this.unregisterReceiver(this.dateTimeChangedService);
        super.onStop();
    }

    @Override
    public void onDataSet(MonthlyGoalsViewModel data) {

        if(data == null)
            throw new IllegalArgumentException("Data is null");

        this.activityViewModel = data;

        if(this.monthlyGoalsActivityLayout == null) {
            if(this.monthlyGoalsActivityLayout == null)
                throw new IllegalArgumentException("monthlyGoalsActivityLayout is null");
        }

        if(this.mainActivityContentBinding == null)
            this.mainActivityContentBinding =  DataBindingUtil.bind(this.monthlyGoalsActivityLayout);

        this.mainActivityContentBinding.setVariable(BR.presenter, this.presenter);
        this.mainActivityContentBinding.setVariable(BR.vm, data);

        ViewDataBinding monthlyPlansGridContentBinding = DataBindingUtil.bind(this.monthlyPlansGridContentLayout);

        monthlyPlansGridContentBinding.setVariable(BR.presenter, this.presenter);
        monthlyPlansGridContentBinding.setVariable(BR.vm, data);


        ViewDataBinding monthlyPlansSummaryContentBinding = DataBindingUtil.bind(this.summaryContentLayout);

        monthlyPlansSummaryContentBinding.setVariable(BR.presenter, this.presenter);
        monthlyPlansSummaryContentBinding.setVariable(BR.vm, data);

        ViewDataBinding emptyContentBinding = DataBindingUtil.findBinding(this.emptyContentHorizontalScrollView);

        emptyContentBinding.setVariable(BR.presenter, this.presenter);
        emptyContentBinding.setVariable(BR.vm, data);

        final RecyclerView dailyPlansRecyclerView = (RecyclerView) this.findViewById(R.id.monthly_goals_header_list_item_days_list);

        if(dailyPlansRecyclerView == null)
            throw new IllegalArgumentException("dailyPlansRecyclerView is null");

        dailyPlansRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View v = dailyPlansRecyclerView;
                v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if(v.getMeasuredHeight() >0 && v.getMeasuredWidth() > 0)
                {
                    scrollToCurrentDate();
                }
            }
        });
    }

    @Override
    public void showDialog(DialogFragment dialogFragment) {
        dialogFragment.show(this.getFragmentManager(), null);
    }

    @Override
    public void onBackPressed() {

        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            this.bottomSheetScrollView.scrollTo(0, 0);
            this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else {
            if(this.exitApplication)
                this.finish();
            else {
                //super.onBackPressed();

                if (this.persistenceContext != null)
                    this.persistenceContext.persistData();
                Toast.makeText(this,
                        R.string.exitApplication_confirm_toast_message,
                        Toast.LENGTH_SHORT).show();
                this.exitApplication = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitApplication = false;
                    }
                }, 3 * 1000);
            }
        }
    }

    private void showDailyProgressDialog() {
        DateTime lastlaunchDateTime = this.settingsManager.getLastLaunchDateTime();

        if(lastlaunchDateTime == null
                || DateTimeHelper.isDateBefore(lastlaunchDateTime, DateTimeHelper.getTodayDateTime()))
        {
            PlansSummaryDescriptionProvider.PlansSummaryDescription description
                    = this.descriptionProvider.provideDescriptionForMonth(DateTimeHelper.getCurrentYear(), DateTimeHelper.getCurrentMonth());
            if(description != null) {
                DailyProgressDialog dialog = new DailyProgressDialog();
                dialog.setModel(description);
                this.showDialog(dialog);
            }
        }

        this.settingsManager.setLastLaunchDateTimeMillis(DateTimeHelper.getNowDateTime());
    }

    private void onCreateGlobalLayoutAvailable()
    {
        ViewBinder.bindViews(this);

        this.setSupportActionBar((Toolbar)this.findViewById(R.id.toolbar));
        this.setupPresenter();
        this.setupRecyclerViews();
        this.setupBottomSheetBehavior();
        this.runStartupCommands();
        this.showDailyProgressDialog();
    }

    private void setupPresenter() {
        this.presenter = this.presentersSource.getMonthlyGoalsPresenter(this);
        this.presenter.initializeWithView(this);
    }

    private void setupBottomSheetBehavior() {
        this.bottomSheetScrollView = (NestedScrollView) this.findViewById(R.id.monthly_goals_activity_bottom_sheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(this.bottomSheetScrollView);
        this.bottomSheetBehavior.setPeekHeight(150);
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setupRecyclerViews()
    {
        ScrollableViewHelper.setSimultaneousScrolling(this.categoryNamesRecyclerView, this.categoryPlansRecyclerView);
    }

    private void runStartupCommands() {
        this.autoMarkTasksCommand.execute(this.findViewById(R.id.monthly_goals_activity_main_coordinator_layout));
    }

    private void injectDependencies() {
        ComposableApplication capp = (ComposableApplication)this.getApplication();
        capp.getApplicationComponent().inject(this);
    }

    private void scrollToCurrentDate()
    {
        if(this.activityViewModel.getMonth() != DateTimeHelper.getCurrentMonth()
                || this.activityViewModel.getYear() != DateTimeHelper.getCurrentYear())
            return;

        HorizontalScrollView daysPlanScrollView = (HorizontalScrollView) this.findViewById(R.id.monthly_goals_table_horizontalScrollView);
        int dayToScrollTo = DateTimeHelper.currentDayNumber() - 3;
        dayToScrollTo = (dayToScrollTo > 0 ? dayToScrollTo : 0);
        int width = (int) this.getResources().getDimension(R.dimen.monthly_goals_table_day_plan_column_width);
        daysPlanScrollView.smoothScrollTo(dayToScrollTo * width, 0);
    }
}
