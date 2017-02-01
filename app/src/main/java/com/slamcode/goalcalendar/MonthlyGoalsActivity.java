package com.slamcode.goalcalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
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
import android.widget.ListView;
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
import com.slamcode.goalcalendar.view.CategoryListViewAdapter;
import com.slamcode.goalcalendar.view.ResourcesHelper;
import com.slamcode.goalcalendar.view.activity.ActivityViewState;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.lists.ListViewDataAdapter;
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
    ListView monthlyGoalsListView;

    @BindView(R.id.monthly_goals_dailyplans_listview)
    ListView dailyPlansListView;

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
        this.showMonthSelectedToast();
    }

    private void showMonthSelectedToast() {
        Toast.makeText(
                this,
                String.format(this.getString(R.string.monthlyPlans_monthSelected_toast_message),
                        ResourcesHelper.toResourceString(this, this.viewModel.getSelectedMonth()),
                        this.viewModel.getSelectedYear()),
                Toast.LENGTH_SHORT
                ).show();
    }

    private void setupSwipeListener()
    {
        SwipeDetector detector = new SwipeDetector();
        this.tableHorizontalScrollView.setOnTouchListener(detector);

        this.emptyContentHorizontalScrollView.setOnTouchListener(detector);
    }

    private void setupMonthlyPlanningCategoryList() {

        if(this.viewModel == null)
        {
            this.viewModel = new MonthlyGoalsViewModel(this, this.getLayoutInflater(), this.persistenceContext);
        }
        // month spinner
        final ArrayAdapter<String> monthsStringsAdapter = new ArrayAdapter<String>(
                this,
                R.layout.monthly_goals_month_spinner_item_layout,
                ResourcesHelper.monthsResourceStrings(this));
        this.monthListSpinner.setAdapter(monthsStringsAdapter);

        this.setupAdapterListeners(this.viewModel.getMonthlyPlannedCategoryListViewAdapter());
        ListViewHelper.setSimultaneousScrolling(this.monthlyGoalsListView, this.dailyPlansListView);

        this.monthlyGoalsListView.setAdapter(this.viewModel.getMonthlyPlannedCategoryListViewAdapter());
        this.dailyPlansListView.setAdapter(this.viewModel.getMonthlyPlannedCategoryListViewAdapter());

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
        this.monthlyGoalsListView.setAdapter(this.viewModel.getMonthlyPlannedCategoryListViewAdapter());
        this.dailyPlansListView.setAdapter(this.viewModel.getMonthlyPlannedCategoryListViewAdapter());
    }

    private void setupAdapterListeners(final CategoryListViewAdapter adapter)
    {
        adapter.addItemSourceChangedEventListener(new ListViewDataAdapter.ItemsSourceChangedEventListener<CategoryModel>() {
            @Override
            public void onNewItemAdded(int itemPosition) {
                monthlyGoalsListView.smoothScrollToPosition(itemPosition);
                dailyPlansListView.smoothScrollToPosition(itemPosition);
                setEmptyListContent();
            }

            @Override
            public void onItemModified(int itemPosition) {
                if(adapter.getCount() == 1)
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

    private class SwipeDetector implements View.OnTouchListener {

        private static final float MIN_DISTANCE = 20;
        private int SCROLL_X_PX_BUFFER = 10;

        private float lastDownX;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    this.lastDownX = event.getX();
                }
                case MotionEvent.ACTION_UP: {
                    float upX = event.getX();
                    float deltaX = this.lastDownX - upX;

                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe();
                        }
                    }
                }
            }
            return false;
        }

        private void onRightToLeftSwipe() {
            if(emptyListLayout.getVisibility() == View.VISIBLE )
                goToNextMonth();
            else{
                int maxScrollX = tableHorizontalScrollView.getChildAt(0).getMeasuredWidth()- tableHorizontalScrollView.getMeasuredWidth();
                int scrollX = tableHorizontalScrollView.getScrollX();
                if (scrollX >= maxScrollX - SCROLL_X_PX_BUFFER)
                    goToNextMonth();
            }
        }

        private void onLeftToRightSwipe() {
            if(emptyListLayout.getVisibility() == View.VISIBLE )
                goToPreviousMonth();
            else{
                int scrollX = tableHorizontalScrollView.getScrollX();
                if (scrollX <= SCROLL_X_PX_BUFFER )
                    goToPreviousMonth();
            }
        }
    }
}
