package com.slamcode.goalcalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.data.*;
import com.slamcode.goalcalendar.data.model.*;
import com.slamcode.goalcalendar.planning.*;
import com.slamcode.goalcalendar.view.AddEditCategoryDialog;
import com.slamcode.goalcalendar.view.CategoryListViewAdapter;
import com.slamcode.goalcalendar.view.ResourcesHelper;
import com.slamcode.goalcalendar.view.activity.ActivityViewStateProvider;
import com.slamcode.goalcalendar.view.lists.ListViewDataAdapter;
import com.slamcode.goalcalendar.view.utils.ColorsHelper;
import com.slamcode.goalcalendar.view.lists.ListViewHelper;
import com.slamcode.goalcalendar.view.utils.SpinnerHelper;

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

    // constructed elements
    private CategoryListViewAdapter categoryListViewAdapter;

    private MonthlyPlansModel selectedMonthlyPlansModel;

    // dependencies
    @Inject
    PersistenceContext persistenceContext;

    @Inject
    ActivityViewStateProvider viewStateProvider;

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
            ListView listView = (ListView) view;
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo
                    = (AdapterView.AdapterContextMenuInfo) menuInfo;
            CategoryModel item = (CategoryModel) listView.getItemAtPosition(adapterContextMenuInfo.position);
            if(item == null)
            {
                return;
            }

            menu.setHeaderTitle(item.getName());
            menu.add(R.string.context_menu_edit_item);
            menu.add(R.string.context_menu_delete_item);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        CharSequence menuItem = item.getTitle();
        CategoryModel model = this.categoryListViewAdapter.getItem(info.position);
        if(getResources().getString(R.string.context_menu_edit_item).equals(menuItem)) {
            this.showAddEditCategoryDialog(model);
        }
        else if(getResources().getString(R.string.context_menu_delete_item).equals(menuItem))
        {
            this.showConfirmDeleteCategoryDialog(model);
        }
        return true;
    }

    private void injectDependencies() {
        ComposableApplication capp = (ComposableApplication)this.getApplication();
        capp.getApplicationComponent().inject(this);
    }

    private void showConfirmDeleteCategoryDialog(final CategoryModel model) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder
                .setTitle(R.string.confirm_delete_category_dialog_header)
                .setMessage(model.getName())
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        categoryListViewAdapter.removeItem(model);
                        dialogInterface.dismiss();
                    }
                })
        .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });

        dialogBuilder.create().show();
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

    @Override
    protected void onStop() {
        if(this.persistenceContext != null)
        {
            this.persistenceContext.persistData();
        }
        super.onStop();
    }

    @OnClick(R.id.monthly_goals_add_category_floatingactionbutton)
    void showAddNewCategoryDialog()
    {
        this.showAddEditCategoryDialog(null);
    }

    private void showAddEditCategoryDialog(CategoryModel model)
    {
        final AddEditCategoryDialog dialog = new AddEditCategoryDialog();
        dialog.setModel(model);
        dialog.setDialogStateChangedListener(new AddEditCategoryDialog.DialogStateChangedListener() {
            @Override
            public void onDialogClosed(boolean confirmed) {
                if(confirmed)
                {
                    CategoryModel newCategory = dialog.getModel();
                    categoryListViewAdapter.addOrUpdateItem(newCategory);
                }
            }
        });
        dialog.show(getFragmentManager(), null);
    }

    @OnItemSelected(R.id.monthly_goals_list_header_month_spinner)
    void onMonthSelected(AdapterView<?> adapterView, View view, int position, long id) {

        Month m = Month.getMonthByNumber(position+1);
        setupCategoryListForMonth(
                this.selectedMonthlyPlansModel != null
                    ? this.selectedMonthlyPlansModel.getYear()
                        : DateTimeHelper.getCurrentYear(),
                m);
    }

    private void setupMonthlyPlanningCategoryList() {

        // month spinner
        final ArrayAdapter<String> monthsStringsAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ResourcesHelper.monthsResourceStrings(this));
        this.monthListSpinner.setAdapter(monthsStringsAdapter);

        /// categories list adapter
        this.categoryListViewAdapter = this.provideMonthCategoriesListViewAdapter();

        ListViewHelper.setSimultaneousScrolling(this.monthlyGoalsListView, this.dailyPlansListView);

        this.monthlyGoalsListView.setAdapter(this.categoryListViewAdapter);
        this.dailyPlansListView.setAdapter(this.categoryListViewAdapter);

        this.registerForContextMenu(this.monthlyGoalsListView);

        setupCategoryListForMonth(DateTimeHelper.getCurrentYear(), Month.getCurrentMonth());
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
    }

    private void scrollToCurrentDay()
    {
        int viewIndexToScrollTo = DateTimeHelper.currentDayNumber() - 3;
        View childView = this.daysNumbersHeaderView.getChildAt(viewIndexToScrollTo);
        if(childView != null) {
            this.tableHorizontalScrollView.smoothScrollTo(childView.getLeft(), 0);
        }
    }

    private void setEmptyListContent(final MonthlyPlansModel currentMonthlyPlans)
    {
        int listLayoutVisibility = this.selectedMonthlyPlansModel != null
                && !this.categoryListViewAdapter.isEmpty() ?
                View.INVISIBLE :
                View.VISIBLE;
        this.emptyListLayout.setVisibility(listLayoutVisibility);

        UnitOfWork uow = this.persistenceContext.createUnitOfWork();
        final MonthlyPlansModel previousMonthWithCategories = uow.getMonthlyPlansRepository().findLast(new Predicate<MonthlyPlansModel>() {
                    @Override
                    public boolean apply(MonthlyPlansModel monthlyPlansModel) {
                        if(monthlyPlansModel.getMonth().getNumValue() < monthlyPlansModel.getMonth().getNumValue())
                        {
                            return false;
                        }

                        return !monthlyPlansModel.getCategories().isEmpty();
                    }
                });
        uow.complete();

        Button copyCategoriesButton = (Button)this.emptyListLayout.findViewById(R.id.monthly_plans_empty_view_copyLastUsedCategories_button);
        TextView emptyContentTextView = (TextView) this.emptyListLayout.findViewById(R.id.monthly_goals_empty_view_content_textView);
        if(previousMonthWithCategories == null || listLayoutVisibility == View.INVISIBLE)
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

                    int year = currentMonthlyPlans.getYear();
                    Month currentMonth = currentMonthlyPlans.getMonth();
                    for(CategoryModel category : previousMonthWithCategories.getCategories())
                    {
                        CategoryModel newCategory = new CategoryModel(
                                category.getId() ^ currentMonth.getNumValue(),
                                category.getName(),
                                category.getPeriod(),
                                category.getFrequencyValue());
                        newCategory.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, currentMonth));

                        categoryListViewAdapter.addOrUpdateItem(newCategory);
                    }
                }
            });
        }

        this.emptyListLayout.invalidate();
    }

    private void resetMonthCategoriesListView()
    {
        this.categoryListViewAdapter = provideMonthCategoriesListViewAdapter();
        this.categoryListViewAdapter.updateMonthlyPlans(this.selectedMonthlyPlansModel);

        this.monthlyGoalsListView.setAdapter(this.categoryListViewAdapter);
        this.dailyPlansListView.setAdapter(this.categoryListViewAdapter);
    }

    private CategoryListViewAdapter provideMonthCategoriesListViewAdapter()
    {
        CategoryListViewAdapter adapter = new CategoryListViewAdapter(this, getLayoutInflater());
        adapter.addItemSourceChangedEventListener(new ListViewDataAdapter.ItemsSourceChangedEventListener<CategoryModel>() {
            @Override
            public void onNewItemAdded(int itemPosition) {
                monthlyGoalsListView.smoothScrollToPosition(itemPosition);
                dailyPlansListView.smoothScrollToPosition(itemPosition);
                setEmptyListContent(selectedMonthlyPlansModel);
            }

            @Override
            public void onItemModified(int itemPosition) {
                if(categoryListViewAdapter.getCount() == 1)
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
                setEmptyListContent(selectedMonthlyPlansModel);
                itemRemovedToast.show();
            }
        });

        return adapter;
    }

    private void setupCategoryListForMonth(int year, Month month)
    {
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();
        MonthlyPlansModel model = uow.getMonthlyPlansRepository().findForMonth(year, month);

        if(model == null)
        {
            model = new MonthlyPlansModel();
            // todo: find good way to assign ids
            model.setId(year ^ month.getNumValue());
            model.setYear(year);
            model.setMonth(month);
            uow.getMonthlyPlansRepository().add(model);
        }

        this.setupHeaderForCategoryListForMonth(model);
        this.categoryListViewAdapter.updateMonthlyPlans(model);
        setEmptyListContent(model);

        this.selectedMonthlyPlansModel = model;

        uow.complete();
    }

    private void setupHeaderForCategoryListForMonth(final MonthlyPlansModel monthlyPlansValue)
    {
        final LayoutInflater inflater = this.getLayoutInflater();
        SpinnerHelper.setSelectedValue(this.monthListSpinner, ResourcesHelper.toResourceStringId(monthlyPlansValue.getMonth()));

        this.daysNumbersHeaderView.removeAllViews();

        //month days list
        List<Integer> listOfDays = CollectionUtils.createList(monthlyPlansValue.getMonth().getDaysCount(), new ElementCreator<Integer>() {

            @Override
            public Integer Create(int index, List<Integer> currentList) {
                return index+1;
            }
        });

        IteratorUtils.forEach(listOfDays.iterator(), new Closure<Integer>() {
            @Override
            public void execute(Integer input) {

                View dayNumberCell = inflater.inflate(R.layout.monthly_goals_header_day_number_cell, null);
                TextView dayNumberText = (TextView) dayNumberCell.findViewById(R.id.monthly_goals_table_header_day_number_text);
                dayNumberText.setText(input.toString());

                TextView dayNameText = (TextView) dayNumberCell.findViewById(R.id.monthly_goals_table_header_day_name_text);
                dayNameText.setText(DateTimeHelper.getWeekDayNameShort(
                        monthlyPlansValue.getYear(),
                        monthlyPlansValue.getMonth().getNumValue(),
                        input));

                boolean isCurrentDate = isCurrentDate(monthlyPlansValue, input);
                if(isCurrentDate)
                {
                    ColorsHelper.setSecondAccentBackgroundColor(dayNumberCell);
                }
                daysNumbersHeaderView.addView(dayNumberCell);
            }
        });
    }

    private boolean isCurrentDate(MonthlyPlansModel model, int dayNumber)
    {
        int year = model.getYear() > 0 ?
                model.getYear() : DateTimeHelper.getCurrentYear();

        return DateTimeHelper.isCurrentDate(
                year,
                model.getMonth().getNumValue(),
                dayNumber);
    }
}
