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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.slamcode.goalcalendar.commands.Command;
import com.slamcode.goalcalendar.commands.CommandStatus;
import com.slamcode.goalcalendar.onboarding.OnBoardingActivity;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangedService;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryDescriptionProvider;
import com.slamcode.goalcalendar.service.commands.DailyProgressInfoCommand;
import com.slamcode.goalcalendar.service.notification.NotificationScheduler;
import com.slamcode.goalcalendar.settings.AppSettingsManager;
import com.slamcode.goalcalendar.settings.SettingsKeys;
import com.slamcode.goalcalendar.view.activity.MonthlyGoalsActivityContract;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.service.commands.AutoMarkTasksCommand;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.lists.ItemsCollectionAdapterProvider;
import com.slamcode.goalcalendar.view.presenters.PresentersSource;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.view.utils.ViewBinder;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

import javax.inject.Inject;

public class MonthlyGoalsActivity extends AppCompatActivity
        implements MonthlyGoalsActivityContract.ActivityView, AppSettingsManager.SettingsChangedListener{

    public final static String STARTED_FROM_PARENT_INTENT_PARAM = "STARTED_FROM_PARENT";

    final String ACTIVITY_ID = MonthlyGoalsActivity.class.getName();

    private final static String LOG_TAG = "GOAL_MGoalsAct";

    View mainLayout;

    @ViewReference(R.id.monthly_goals_activity_main_coordinator_layout)
    CoordinatorLayout monthlyGoalsActivityLayout;

    @ViewReference(R.id.monthly_goals_listview)
    RecyclerView categoryNamesRecyclerView;

    @ViewReference(R.id.content_monthly_goals)
    RelativeLayout monthlyPlansGridContentLayout;

    @ViewReference(R.id.monthly_goals_emptyContent_horizontallScrollView)
    HorizontalScrollView emptyContentHorizontalScrollView;

    @ViewReference(R.id.monthly_goals_summary_content_layout)
    LinearLayout summaryContentLayout;

    private LinearLayout emptyContentLayout;

    private NestedScrollView bottomSheetScrollView;

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
    DailyProgressInfoCommand dailyProgressInfoCommand;

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

    private boolean exitApplication;

    private boolean dataBindingsSetUp;
    private boolean needToRecreate;
    private int currentThemeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Monthly goals activity creating - START");
        super.onCreate(savedInstanceState);
        this.injectDependencies();
        this.setTheme(this.settingsManager.getThemeId());
        this.currentThemeId = this.settingsManager.getThemeId();
        this.settingsManager.addSettingsChangedListener(this);
        setContentView(com.slamcode.goalcalendar.R.layout.monthly_goals_activity);
        Log.d(LOG_TAG, "Monthly goals activity view set");

        this.findAllRelatedViews();
        Log.d(LOG_TAG, "Monthly goals dependencies injected");
        if(this.doStartOnBoarding())
            this.startOnBoardingActivity();

        this.startMainActivity();
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

        if (id == R.id.action_backups) {
            Intent goToSettings = new Intent(this, BackupActivity.class);
            this.startActivity(goToSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setupDataBindings();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        this.registerReceiver(this.dateTimeChangedService, intentFilter);
    }

    @Override
    protected void onStop() {
        if(this.persistenceContext != null)
            this.persistenceContext.persistData();
        this.unregisterReceiver(this.dateTimeChangedService);
        //this.unregisterReceiver(this.notificationServiceStarterReceiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.needToRecreate)
            this.recreate();
    }

    @Override
    public void onDataSet(MonthlyGoalsViewModel data) {

        if(data == null)
            throw new IllegalArgumentException("Data is null");

        this.activityViewModel = data;

        this.setupDataBindings();
    }

    private void setupDataBindings()
    {
        if(dataBindingsSetUp
                || this.activityViewModel == null)
            return;
        MonthlyGoalsViewModel data = this.activityViewModel;
        if(this.monthlyGoalsActivityLayout == null) {
            this.findAllRelatedViews();

            if(this.monthlyGoalsActivityLayout == null)
                throw new IllegalArgumentException("monthlyGoalsActivityLayout is null and can not be found in activity view");
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

        ViewDataBinding emptyContentBinding = DataBindingUtil.bind(this.emptyContentHorizontalScrollView);

        emptyContentBinding.setVariable(BR.presenter, this.presenter);
        emptyContentBinding.setVariable(BR.vm, data);

        this.dataBindingsSetUp = true;
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

    private void findAllRelatedViews()
    {
        this.mainLayout = this.findViewById(android.R.id.content);
        this.monthlyGoalsActivityLayout = ViewBinder.findView(this, R.id.monthly_goals_activity_main_coordinator_layout);
        this.categoryNamesRecyclerView = ViewBinder.findView(this, R.id.monthly_goals_listview);
        this.monthlyPlansGridContentLayout = ViewBinder.findView(this, R.id.content_monthly_goals);
        this.emptyContentLayout = ViewBinder.findView(this, R.id.monthly_goals_emptyContent_layout);
        this.emptyContentHorizontalScrollView = ViewBinder.findView(this, R.id.monthly_goals_emptyContent_horizontallScrollView);
        this.summaryContentLayout = ViewBinder.findView(this, R.id.monthly_goals_summary_content_layout);
        this.bottomSheetScrollView = ViewBinder.findView(this, R.id.monthly_goals_activity_bottom_sheet);
    }

    private  boolean doStartOnBoarding()
    {
        if(BuildConfig.DEBUG &&
                (this.settingsManager.getOnboardingShownDate() == null
                    || DateTimeHelper.isDateBefore(this.settingsManager.getOnboardingShownDate(), DateTimeHelper.getTodayDateTime())))
            return true;

        return this.settingsManager.getLastLaunchDateTime() == null
                && !this.originatedFromNotification();
    }

    private boolean originatedFromNotification()
    {
        return this.getIntent().getBooleanExtra(NotificationScheduler.NOTIFICATION_ORIGINATED_FROM_FLAG, false);
    }

    private void startOnBoardingActivity()
    {
        Intent intent = new Intent(this, OnBoardingActivity.class);
        intent.putExtra(STARTED_FROM_PARENT_INTENT_PARAM, true);
        this.settingsManager.setOnboardingShownDate(DateTimeHelper.getNowDateTime());
        this.startActivity(intent);
    }

    private void startMainActivity()
    {
        this.mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                View v = mainLayout;
                v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                onCreateGlobalLayoutAvailable();
                Log.d(LOG_TAG, "Monthly goals activity view rendered");
            }
        });
    }

    private void onCreateGlobalLayoutAvailable()
    {
        this.setSupportActionBar((Toolbar)this.findViewById(R.id.toolbar));
        this.startServices();
        this.setupPresenter();
        this.setupBottomSheetBehavior();
        this.runStartupCommands();
    }

    private void setupPresenter() {
        this.presenter = this.presentersSource.getMonthlyGoalsPresenter(this);
        this.presenter.initializeWithView(this);
    }

    private void setupBottomSheetBehavior() {
        this.bottomSheetScrollView = (NestedScrollView) this.findViewById(R.id.monthly_goals_activity_bottom_sheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(this.bottomSheetScrollView);
        this.bottomSheetBehavior.setPeekHeight(this.getResources().getDimensionPixelSize(R.dimen.monthly_goals_summary_bottomSheet_peekHeight));
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void runStartupCommands() {

        this.dailyProgressInfoCommand.addCommandStateChangedListener(new Command.CommandStateChangedListener() {
            @Override
            public void onStateChanged(CommandStatus oldStatus, CommandStatus newStatus) {
                if(newStatus == CommandStatus.Executed)
                    autoMarkTasksCommand.execute(findViewById(R.id.monthly_goals_activity_main_coordinator_layout));
            }
        });

        this.dailyProgressInfoCommand.execute(this);
    }

    private void startServices()
    {
        this.startService(new Intent(this, NotificationScheduler.class));
    }

    private void injectDependencies() {
        ComposableApplication capp = (ComposableApplication)this.getApplication();
        capp.getApplicationComponent().inject(this);
    }

    public void scrollToCurrentDate()
    {
        if(this.activityViewModel.getMonth() != DateTimeHelper.getCurrentMonth()
                || this.activityViewModel.getYear() != DateTimeHelper.getCurrentYear())
            return;

        HorizontalScrollView daysPlanScrollView = (HorizontalScrollView) this.findViewById(R.id.monthly_goals_list_header_days_list_horizontal_scroll_view);
        int dayToScrollTo = DateTimeHelper.currentDayNumber() - 3;
        dayToScrollTo = (dayToScrollTo > 0 ? dayToScrollTo : 0);
        int width = (int) this.getResources().getDimension(R.dimen.monthly_goals_table_day_plan_column_width);
        daysPlanScrollView.smoothScrollTo(dayToScrollTo * width, 0);
    }

    @Override
    public void settingChanged(String settingId) {
        if(settingId.equals(SettingsKeys.THEME_ID_NAME))
        {
            this.needToRecreate = this.currentThemeId != this.settingsManager.getThemeId();
        }
    }
}
