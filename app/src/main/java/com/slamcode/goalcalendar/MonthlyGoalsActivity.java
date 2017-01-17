package com.slamcode.goalcalendar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.*;
import com.slamcode.goalcalendar.view.CategoryListViewAdapter;
import com.slamcode.goalcalendar.view.ResourcesHelper;
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

public class MonthlyGoalsActivity extends AppCompatActivity {

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

    @BindView(R.id.monthly_goals_emptyListView)
    LinearLayout emptyListLayout;

    @BindView(R.id.monthly_goals_year_textView)
    TextView yearTextView;

    @BindView(R.id.monthly_goals_decrement_year_button)
    ImageButton decrementYearButton;

    @BindView(R.id.monthly_goals_increment_year_button)
    ImageButton incrementYearButton;

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.slamcode.goalcalendar.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.monthly_goals_decrement_year_button)
    void goToPreviousYear()
    {
        int year = this.viewModel.getCurrentYear() -1;
        this.setupUiForYearAndMonth(year, this.viewModel.getCurrentMonth());
    }

    @OnClick(R.id.monthly_goals_increment_year_button)
    void goToNextYear()
    {
        int year = this.viewModel.getCurrentYear() +1;
        this.setupUiForYearAndMonth(year, this.viewModel.getCurrentMonth());
    }

    @OnClick(R.id.monthly_goals_add_category_floatingactionbutton)
    void showAddNewCategoryDialog()
    {
        this.viewModel.createAddEditCategoryDialog(-1).show(this.getFragmentManager(), null);
    }

    @OnItemSelected(R.id.monthly_goals_list_header_month_spinner)
    void onMonthSelected(AdapterView<?> adapterView, View view, int position, long id) {

        Month month = Month.getMonthByNumber(position+1);
        this.setupUiForYearAndMonth(this.viewModel.getCurrentYear(), month);
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

    private void setupMonthlyPlanningCategoryList() {

        if(this.viewModel == null)
        {
            this.viewModel = new MonthlyGoalsViewModel(this, this.getLayoutInflater(), this.persistenceContext);
        }
        // month spinner
        final ArrayAdapter<String> monthsStringsAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ResourcesHelper.monthsResourceStrings(this));
        this.monthListSpinner.setAdapter(monthsStringsAdapter);

        this.setupAdapterListeners(this.viewModel.getMonthlyPlannedCategoryListViewAdapter());
        ListViewHelper.setSimultaneousScrolling(this.monthlyGoalsListView, this.dailyPlansListView);

        this.monthlyGoalsListView.setAdapter(this.viewModel.getMonthlyPlannedCategoryListViewAdapter());
        this.dailyPlansListView.setAdapter(this.viewModel.getMonthlyPlannedCategoryListViewAdapter());

        this.registerForContextMenu(this.monthlyGoalsListView);

        this.viewModel.setYearAndMonth(DateTimeHelper.getCurrentYear(), Month.getCurrentMonth());
        if(!this.viewStateProvider.provideStateForActivity(ACTIVITY_ID).isWasDisplayed()) {
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
        this.viewModel.setYearAndMonth(this.viewModel.getCurrentYear(), this.viewModel.getCurrentMonth());
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
        this.yearTextView.setText(String.format("%s", this.viewModel.getCurrentYear()));

        final LayoutInflater inflater = this.getLayoutInflater();
        SpinnerHelper.setSelectedValue(this.monthListSpinner, ResourcesHelper.toResourceStringId(this.viewModel.getCurrentMonth()));

        this.daysNumbersHeaderView.removeAllViews();

        //month days list
        List<Integer> listOfDays = CollectionUtils.createList(
                this.viewModel.getCurrentMonth()
                        .getDaysCount(this.viewModel.getCurrentYear()),
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
                        viewModel.getCurrentYear(),
                        viewModel.getCurrentMonth().getNumValue(),
                        dayNumber));

                boolean isCurrentDate = DateTimeHelper.isCurrentDate(
                        viewModel.getCurrentYear(),
                        viewModel.getCurrentMonth().getNumValue(),
                        dayNumber);
                if(isCurrentDate)
                {
                    ColorsHelper.setSecondAccentBackgroundColor(dayNumberCell);
                }
                daysNumbersHeaderView.addView(dayNumberCell);
            }
        });
    }
}
