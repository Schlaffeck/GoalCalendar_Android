package com.slamcode.goalcalendar;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.*;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.databinding.PlansMonthlySummaryContentViewBinding;
import com.slamcode.goalcalendar.planning.*;
import com.slamcode.goalcalendar.service.commands.AutoMarkTasksCommand;
import com.slamcode.goalcalendar.service.notification.NotificationScheduler;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.ResourcesHelper;
import com.slamcode.goalcalendar.view.activity.ActivityViewState;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.lists.ListAdapterProvider;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.presenters.MonthlyGoalsPresenter;
import com.slamcode.goalcalendar.view.viewmodels.MonthlyProgressSummaryViewModel;
import com.slamcode.goalcalendar.view.presenters.PresentersSource;
import com.slamcode.goalcalendar.view.utils.ColorsHelper;
import com.slamcode.goalcalendar.view.lists.ScrollableViewHelper;
import com.slamcode.goalcalendar.view.utils.SpinnerHelper;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IteratorUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class MonthlyGoalsActivity extends AppCompatActivity{

    final String ACTIVITY_ID = MonthlyGoalsActivity.class.getName();

    // view elements
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView((R.id.monthly_goals_add_category_floatingactionbutton))
    FloatingActionButton floatingActionButton;

    @BindView((R.id.monthly_goals_list_header_month_spinner))
    Spinner monthListSpinner;

    @BindView(R.id.monthly_goals_listview)
    RecyclerView monthlyGoalsListView;

    @BindView(R.id.monthly_goals_dailyplans_listview)
    RecyclerView dailyPlansListView;

    @BindView(R.id.monthly_goals_header_list_item_days_list)
    LinearLayout daysNumbersHeaderView;

    @BindView(R.id.monthly_goals_table_horizontalScrollView)
    HorizontalScrollView tableHorizontalScrollView;

    @BindView(R.id.monthly_goals_emptyContent_layout)
    LinearLayout emptyContentLayout;

    @BindView(R.id.monthly_goals_emptyListView)
    LinearLayout emptyListLayout;

    @BindView(R.id.monthly_goals_year_textView)
    TextView yearTextView;

    @BindView(R.id.monthly_goals_decrement_year_button)
    ImageButton decrementYearButton;

    @BindView(R.id.monthly_goals_increment_year_button)
    ImageButton incrementYearButton;

    @BindView(R.id.monthly_goals_emptyContent_horizontallScrollView)
    HorizontalScrollView emptyContentHorizontalScrollView;

    @BindView(R.id.monthly_goals_activity_bottom_sheet)
    NestedScrollView bottomSheetScrollView;

    @BindView(R.id.monthly_goals_summary_generalProgress_textView)
    TextView summaryGeneralPercentageTextView;

    private BottomSheetBehavior bottomSheetBehavior;

    private MonthlyGoalsPresenter presenter;

    // dependencies
    @Inject
    ActivityViewStateProvider viewStateProvider;

    @Inject
    PersistenceContext persistenceContext;

    @Inject
    ListAdapterProvider adapterProvider;
    
    @Inject
    AutoMarkTasksCommand autoMarkTasksCommand;

    @Inject
    PresentersSource presentersSource;

    private GestureDetectorCompat gestureDetector;

    private PlansMonthlySummaryContentViewBinding monthlySummaryContentViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.slamcode.goalcalendar.R.layout.monthly_goals_activity);

        this.injectDependencies();
        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setupMonthlyPlanningCategoryList();
        this.setupSwipeListener();
        this.runStartupCommands();
    }

    private void setupBottomSheetBehavior() {
        this.bottomSheetBehavior = BottomSheetBehavior.from(this.bottomSheetScrollView);
        this.bottomSheetBehavior.setPeekHeight(150);
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        this.monthlySummaryContentViewBinding = DataBindingUtil.bind(this.bottomSheetScrollView.getChildAt(0));
        this.monthlySummaryContentViewBinding.setVm(this.presenter.getProgressSummaryValue());
        this.presenter.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if(i == com.android.databinding.library.baseAdapters.BR.progressSummaryValue)
                    summaryViewModelChanged();
            }
        });
    }

    private void summaryViewModelChanged()
    {
        MonthlyProgressSummaryViewModel viewModel = this.presenter.getProgressSummaryValue();
            if(this.monthlySummaryContentViewBinding != null
                    && this.monthlySummaryContentViewBinding.getVm() != viewModel)
                this.monthlySummaryContentViewBinding.setVm(viewModel);
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

    private void goToPreviousMonth()
    {
        Month month = Month.getPreviousMonth(this.presenter.getSelectedMonth());

        this.setupUiForYearAndMonth(
                month == Month.DECEMBER ?
                        this.presenter.getSelectedYear() -1 :
                        this.presenter.getSelectedYear(),
                month);
    }

    private void goToNextMonth()
    {
        Month month = Month.getNextMonth(this.presenter.getSelectedMonth());

        this.setupUiForYearAndMonth(
                month == Month.JANUARY ?
                        this.presenter.getSelectedYear() +1 :
                        this.presenter.getSelectedYear(),
                month);
    }

    @OnClick(R.id.monthly_goals_decrement_year_button)
    void goToPreviousYear()
    {
        int year = this.presenter.getSelectedYear() -1;
        this.setupUiForYearAndMonth(year, this.presenter.getSelectedMonth());
    }

    @OnClick(R.id.monthly_goals_increment_year_button)
    void goToNextYear()
    {
        int year = this.presenter.getSelectedYear() +1;
        this.setupUiForYearAndMonth(year, this.presenter.getSelectedMonth());
    }

    @OnClick(R.id.monthly_goals_add_category_floatingactionbutton)
    void showAddNewCategoryDialog()
    {
        this.presenter.createAddEditCategoryDialog(-1).show(this.getFragmentManager(), null);
    }

    @OnItemSelected(R.id.monthly_goals_list_header_month_spinner)
    void onMonthSelected(AdapterView<?> adapterView, View view, int position, long id) {

        Month month = Month.getMonthByNumber(position+1);
        this.setupUiForYearAndMonth(this.presenter.getSelectedYear(), month);
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

    private void setupUiForYearAndMonth(int year, Month month)
    {
        this.presenter.setYearAndMonth(year, month);
        this.setupHeaderForCategoryListForMonth();
        this.setEmptyListContent();
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

    private void setupMonthlyPlanningCategoryList() {

        if(this.presenter == null)
        {
            this.presenter = this.presentersSource.getMonthlyGoalsPresenter(this);
            this.presenter.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable observable, int i) {

                    summaryViewModelChanged();
                }
            });
        }
        // month spinner
        final ArrayAdapter<String> monthsStringsAdapter = new ArrayAdapter<String>(
                this,
                R.layout.monthly_goals_month_spinner_item_layout,
                ResourcesHelper.monthsResourceStrings(this));
        this.monthListSpinner.setAdapter(monthsStringsAdapter);

        this.setupAdapterListeners(this.presenter.getCategoryNamesRecyclerViewAdapter());
        ScrollableViewHelper.setSimultaneousScrolling(this.monthlyGoalsListView, this.dailyPlansListView);

        this.monthlyGoalsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.monthlyGoalsListView.setAdapter(this.presenter.getCategoryNamesRecyclerViewAdapter());
        this.dailyPlansListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.dailyPlansListView.setAdapter(this.presenter.getCategoryDailyPlansRecyclerViewAdapter());

        //this.registerForContextMenu(this.monthlyGoalsListView);

        this.presenter.setYearAndMonth(DateTimeHelper.getCurrentYear(), Month.getCurrentMonth());
        if(this.canScrollToCurrentDay()) {
            this.daysNumbersHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    daysNumbersHeaderView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    if (daysNumbersHeaderView.getMeasuredHeight() > 0 && daysNumbersHeaderView.getMeasuredWidth() > 0) {
                        scrollToCurrentDay();
                        viewStateProvider.provideStateForActivity(ACTIVITY_ID).setWasDisplayed(true);
                    }
                }
            });
        }

        this.setupHeaderForCategoryListForMonth();
        this.setupBottomSheetBehavior();
    }

    private boolean canScrollToCurrentDay()
    {
        if(this.presenter.getSelectedMonth() != Month.getCurrentMonth())
            return false;

        ActivityViewState state = this.viewStateProvider.provideStateForActivity(ACTIVITY_ID);
        return !state.isWasDisplayed()
            || NotificationScheduler.checkIfOriginatedFromNotification(this);
    }

    private void scrollToCurrentDay()
    {
        int viewIndexToScrollTo = DateTimeHelper.currentDayNumber() - 3;
        View childView = this.daysNumbersHeaderView.getChildAt(viewIndexToScrollTo);
        if(childView != null) {
            this.tableHorizontalScrollView.smoothScrollTo(childView.getLeft(), 0);
        }
    }

    private void setEmptyListContent()
    {
        boolean listIsEmpty = this.presenter.isEmptyCategoriesList();
        this.emptyListLayout.setVisibility(listIsEmpty ? View.VISIBLE : View.INVISIBLE);

        if(!listIsEmpty)
            return;

        Button copyCategoriesButton = (Button)this.emptyListLayout.findViewById(R.id.monthly_plans_empty_view_copyLastUsedCategories_button);
        TextView emptyContentTextView = (TextView) this.emptyListLayout.findViewById(R.id.monthly_goals_empty_view_content_textView);
        if(!this.presenter.canCopyCategoriesFromPreviousMonth())
        {
            copyCategoriesButton.setVisibility(View.INVISIBLE);
            emptyContentTextView.setText(R.string.monthly_plans_empty_view_simplyAdd_content_text);
        }
        else
        {
            emptyContentTextView.setText(R.string.monthly_plans_empty_view_content_text);
            copyCategoriesButton.setVisibility(View.VISIBLE);

            copyCategoriesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.copyCategoriesFromPreviousMonth();
                    Toast categoriesCopied = Toast.makeText(
                            getApplicationContext(),
                            String.format(getResources().getString(R.string.monthly_plans_empty_view_categories_copied_toast_text)),
                            Toast.LENGTH_SHORT);
                    categoriesCopied.show();
                }
            });
        }

        this.emptyListLayout.invalidate();
    }

    private void resetMonthCategoriesListView()
    {
        this.presenter.setYearAndMonth(this.presenter.getSelectedYear(), this.presenter.getSelectedMonth());
        this.monthlyGoalsListView.setAdapter(this.presenter.getCategoryNamesRecyclerViewAdapter());
        this.dailyPlansListView.setAdapter(this.presenter.getCategoryDailyPlansRecyclerViewAdapter());
    }

    private void setupAdapterListeners(final CategoryNameRecyclerViewAdapter adapter)
    {
        adapter.addItemSourceRequestListener(new RecyclerViewDataAdapter.ItemsSourceChangeRequestListener() {
            @Override
            public void onModifyItemRequest(int itemPosition) {
                presenter.createAddEditCategoryDialog(itemPosition).show(getFragmentManager(), null);
            }

            @Override
            public void onRemoveItemRequest(int itemPosition) {
                presenter.createDeleteCategoryDialog(itemPosition).show();
            }
        });
    }

    private void setupHeaderForCategoryListForMonth()
    {
        this.yearTextView.setText(String.format("%s", this.presenter.getSelectedYear()));

        final LayoutInflater inflater = this.getLayoutInflater();
        SpinnerHelper.setSelectedValue(this.monthListSpinner, ResourcesHelper.toResourceString(this, this.presenter.getSelectedMonth()));

        this.daysNumbersHeaderView.removeAllViews();

        //month days list
        List<Integer> listOfDays = CollectionUtils.createList(
                this.presenter.getSelectedMonth()
                        .getDaysCount(this.presenter.getSelectedYear()),
                new ElementCreator<Integer>() {

            @Override
            public Integer Create(int index, List<Integer> currentList) {
                return index+1;
            }
        });

        IteratorUtils.forEach(listOfDays.iterator(), new Closure<Integer>() {
            @Override
            public void execute(Integer dayNumber) {

                View dayNumberCell = inflater.inflate(R.layout.monthly_goals_header_day_number_cell, null);
                TextView dayNumberText = (TextView) dayNumberCell.findViewById(R.id.monthly_goals_table_header_day_number_text);
                dayNumberText.setText(dayNumber.toString());

                TextView dayNameText = (TextView) dayNumberCell.findViewById(R.id.monthly_goals_table_header_day_name_text);
                dayNameText.setText(DateTimeHelper.getWeekDayNameShort(
                        presenter.getSelectedYear(),
                        presenter.getSelectedMonth().getNumValue(),
                        dayNumber));

                boolean isCurrentDate = DateTimeHelper.isCurrentDate(
                        presenter.getSelectedYear(),
                        presenter.getSelectedMonth().getNumValue(),
                        dayNumber);
                if(isCurrentDate)
                {
                    ColorsHelper.setSecondAccentBackgroundColor(dayNumberCell);
                }
                daysNumbersHeaderView.addView(dayNumberCell);
            }
        });
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
            goToPreviousMonth();
        }

        private void onLeftSwipe() {
            Log.d(LOG_TAG, "Left swipe");
            goToNextMonth();
        }
    }
}
