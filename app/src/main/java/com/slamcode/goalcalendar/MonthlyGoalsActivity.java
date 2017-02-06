package com.slamcode.goalcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
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

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.planning.*;
import com.slamcode.goalcalendar.service.NotificationScheduler;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.ResourcesHelper;
import com.slamcode.goalcalendar.view.activity.ActivityViewState;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.lists.ListAdapterProvider;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.utils.ColorsHelper;
import com.slamcode.goalcalendar.view.lists.ListViewHelper;
import com.slamcode.goalcalendar.view.utils.SpinnerHelper;
import com.slamcode.goalcalendar.view.viewmodel.MonthlyGoalsViewModel;

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
    LinearLayout emptyContentlayout;

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

    //todo: move to di container and inject
    MonthlyGoalsViewModel viewModel;

    // dependencies
    @Inject
    ActivityViewStateProvider viewStateProvider;

    @Inject
    PersistenceContext persistenceContext;

    @Inject
    ListAdapterProvider adapterProvider;

    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.slamcode.goalcalendar.R.layout.monthly_goals_activity);

        this.injectDependencies();
        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.setupMonthlyPlanningCategoryList();
        this.setupSwipeListener();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        if(view.getId() == R.id.monthly_goals_listview)
        {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo
                    = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(this.viewModel.getCategoryNameOnPosition(adapterContextMenuInfo.position));
            menu.add(R.string.context_menu_edit_item);
            menu.add(R.string.context_menu_delete_item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        CharSequence menuItem = item.getTitle();
        if(getResources().getString(R.string.context_menu_edit_item).equals(menuItem))
            this.viewModel.createAddEditCategoryDialog(info.position).show(this.getFragmentManager(), null);
        else if(getResources().getString(R.string.context_menu_delete_item).equals(menuItem))
            this.viewModel.createDeleteCategoryDialog(info.position).show();
        return true;
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
        Month month = Month.getPreviousMonth(this.viewModel.getSelectedMonth());

        this.setupUiForYearAndMonth(
                month == Month.DECEMBER ?
                        this.viewModel.getSelectedYear() -1 :
                        this.viewModel.getSelectedYear(),
                month);
    }

    private void goToNextMonth()
    {
        Month month = Month.getNextMonth(this.viewModel.getSelectedMonth());

        this.setupUiForYearAndMonth(
                month == Month.JANUARY ?
                        this.viewModel.getSelectedYear() +1 :
                        this.viewModel.getSelectedYear(),
                month);
    }

    @OnClick(R.id.monthly_goals_decrement_year_button)
    void goToPreviousYear()
    {
        int year = this.viewModel.getSelectedYear() -1;
        this.setupUiForYearAndMonth(year, this.viewModel.getSelectedMonth());
    }

    @OnClick(R.id.monthly_goals_increment_year_button)
    void goToNextYear()
    {
        int year = this.viewModel.getSelectedYear() +1;
        this.setupUiForYearAndMonth(year, this.viewModel.getSelectedMonth());
    }

    @OnClick(R.id.monthly_goals_add_category_floatingactionbutton)
    void showAddNewCategoryDialog()
    {
        this.viewModel.createAddEditCategoryDialog(-1).show(this.getFragmentManager(), null);
    }

    @OnItemSelected(R.id.monthly_goals_list_header_month_spinner)
    void onMonthSelected(AdapterView<?> adapterView, View view, int position, long id) {

        Month month = Month.getMonthByNumber(position+1);
        this.setupUiForYearAndMonth(this.viewModel.getSelectedYear(), month);
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
        this.viewModel.setYearAndMonth(year, month);
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

        if(this.viewModel == null)
        {
            this.viewModel = new MonthlyGoalsViewModel(this, this.getLayoutInflater(), this.persistenceContext, this.adapterProvider);
        }
        // month spinner
        final ArrayAdapter<String> monthsStringsAdapter = new ArrayAdapter<String>(
                this,
                R.layout.monthly_goals_month_spinner_item_layout,
                ResourcesHelper.monthsResourceStrings(this));
        this.monthListSpinner.setAdapter(monthsStringsAdapter);

        this.setupAdapterListeners(this.viewModel.getCategoryNamesRecyclerViewAdapter());
        ListViewHelper.setSimultaneousScrolling(this.monthlyGoalsListView, this.dailyPlansListView);

        this.monthlyGoalsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.monthlyGoalsListView.setAdapter(this.viewModel.getCategoryNamesRecyclerViewAdapter());
        this.dailyPlansListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.dailyPlansListView.setAdapter(this.viewModel.getCategoryDailyPlansRecyclerViewAdapter());

        this.registerForContextMenu(this.monthlyGoalsListView);

        this.viewModel.setYearAndMonth(DateTimeHelper.getCurrentYear(), Month.getCurrentMonth());
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
    }

    private boolean canScrollToCurrentDay()
    {
        if(this.viewModel.getSelectedMonth() != Month.getCurrentMonth())
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
        boolean listIsEmpty = this.viewModel.isEmptyCategoriesList();
        this.emptyListLayout.setVisibility(listIsEmpty ? View.VISIBLE : View.INVISIBLE);

        if(!listIsEmpty)
            return;

        Button copyCategoriesButton = (Button)this.emptyListLayout.findViewById(R.id.monthly_plans_empty_view_copyLastUsedCategories_button);
        TextView emptyContentTextView = (TextView) this.emptyListLayout.findViewById(R.id.monthly_goals_empty_view_content_textView);
        if(!this.viewModel.canCopyCategoriesFromPreviousMonth())
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
                    viewModel.copyCategoriesFromPreviousMonth();
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
        this.viewModel.setYearAndMonth(this.viewModel.getSelectedYear(), this.viewModel.getSelectedMonth());
        this.monthlyGoalsListView.setAdapter(this.viewModel.getCategoryNamesRecyclerViewAdapter());
        this.dailyPlansListView.setAdapter(this.viewModel.getCategoryDailyPlansRecyclerViewAdapter());
    }

    private void setupAdapterListeners(final CategoryNameRecyclerViewAdapter adapter)
    {
        adapter.addItemSourceChangedEventListener(new RecyclerViewDataAdapter.ItemsSourceChangedEventListener<CategoryModel>() {
            @Override
            public void onNewItemAdded(int itemPosition) {
                monthlyGoalsListView.smoothScrollToPosition(itemPosition);
                dailyPlansListView.smoothScrollToPosition(itemPosition);
                setEmptyListContent();
            }

            @Override
            public void onItemModified(int itemPosition) {
                if(adapter.getItemCount() == 1)
                {
                    // dirty hack for updating modified list with single-item
                    resetMonthCategoriesListView();
                }
                else {
                    monthlyGoalsListView.smoothScrollToPosition(itemPosition);
                    dailyPlansListView.smoothScrollToPosition(itemPosition);
                }
            }

            @Override
            public void onItemRemoved(CategoryModel item) {
                Toast itemRemovedToast = Toast.makeText(
                        getApplicationContext(),
                        String.format("%s: %s", getResources().getString(R.string.confirm_category_deleted_toast), item.getName()),
                        Toast.LENGTH_SHORT);
                setEmptyListContent();
                itemRemovedToast.show();
            }
        });
    }

    private void setupHeaderForCategoryListForMonth()
    {
        this.yearTextView.setText(String.format("%s", this.viewModel.getSelectedYear()));

        final LayoutInflater inflater = this.getLayoutInflater();
        SpinnerHelper.setSelectedValue(this.monthListSpinner, ResourcesHelper.toResourceString(this, this.viewModel.getSelectedMonth()));

        this.daysNumbersHeaderView.removeAllViews();

        //month days list
        List<Integer> listOfDays = CollectionUtils.createList(
                this.viewModel.getSelectedMonth()
                        .getDaysCount(this.viewModel.getSelectedYear()),
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
                        viewModel.getSelectedYear(),
                        viewModel.getSelectedMonth().getNumValue(),
                        dayNumber));

                boolean isCurrentDate = DateTimeHelper.isCurrentDate(
                        viewModel.getSelectedYear(),
                        viewModel.getSelectedMonth().getNumValue(),
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
