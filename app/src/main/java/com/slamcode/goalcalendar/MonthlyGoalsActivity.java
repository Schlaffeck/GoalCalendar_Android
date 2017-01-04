package com.slamcode.goalcalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.dagger2.ComposableApplication;
import com.slamcode.goalcalendar.data.*;
import com.slamcode.goalcalendar.data.inmemory.InMemoryCategoriesRepository;
import com.slamcode.goalcalendar.data.inmemory.InMemoryMonthlyPlansRepository;
import com.slamcode.goalcalendar.data.json.JsonFilePersistenceContext;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.view.AddEditCategoryDialog;
import com.slamcode.goalcalendar.view.CategoriesListViewAdapter;
import com.slamcode.goalcalendar.view.ResourcesHelper;
import com.slamcode.goalcalendar.view.lists.ListViewDataAdapter;
import com.slamcode.goalcalendar.view.utils.SpinnerHelper;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IteratorUtils;

import java.util.List;

import javax.inject.Inject;

public class MonthlyGoalsActivity extends AppCompatActivity {

    private CategoriesListViewAdapter monthListViewAdapter;

    @Inject
    PersistenceContext persistenceContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.injectDependencies();

        setContentView(com.slamcode.goalcalendar.R.layout.monthly_goals_activity);
        Toolbar toolbar = (Toolbar) findViewById(com.slamcode.goalcalendar.R.id.toolbar);
        setSupportActionBar(toolbar);
        setupFloatingButtonAction();
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
        CategoryModel model = (CategoryModel)this.monthListViewAdapter.getItem(info.position);
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
        ((ComposableApplication)this.getApplication()).getDataComponent().inject(this);
    }

    private void showConfirmDeleteCategoryDialog(final CategoryModel model) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder
                .setTitle(R.string.confirm_delete_category_dialog_header)
                .setMessage(model.getName())
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        monthListViewAdapter.removeItem(model);
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

    private void setupFloatingButtonAction()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(com.slamcode.goalcalendar.R.id.monthly_goals_add_category_floatingactionbutton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            showAddEditCategoryDialog(null);
            }
        });
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
                    monthListViewAdapter.addOrUpdateItem(newCategory);
                }
            }
        });
        dialog.show(getFragmentManager(), null);
    }

    private void setupMonthlyPlanningCategoryList() {

        // month spinner
        LinearLayout header = (LinearLayout) this.findViewById(R.id.monthly_goals_header_list_item_month_panel);
        final Spinner monthSpinner = (Spinner) header.findViewById(R.id.monthly_goals_list_header_month_spinner);
        final ArrayAdapter<String> monthsStringsAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                ResourcesHelper.monthsResourceStrings(this));
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                Month m = Month.getMonthByNumber(position+1);
                setupCategoryListForMonth(m);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        monthSpinner.setAdapter(monthsStringsAdapter);

        /// categories list adapter
        this.monthListViewAdapter = new CategoriesListViewAdapter(this, getLayoutInflater());
        final ListView listView = (ListView) this.findViewById(R.id.monthly_goals_listview);
        this.monthListViewAdapter.addItemSourceChangedEventListener(new ListViewDataAdapter.ItemsSourceChangedEventListener<CategoryModel>() {
            @Override
            public void onNewItemAdded(int itemPosition) {
                listView.smoothScrollToPosition(itemPosition);
            }

            @Override
            public void onItemModified(int itemPosition) {
                listView.smoothScrollToPosition(itemPosition);
            }

            @Override
            public void onItemRemoved(CategoryModel item) {
                Toast itemRemovedToast = Toast.makeText(
                        getApplicationContext(),
                        String.format("%s: %s", getResources().getString(R.string.confirm_category_deleted_toast), item.getName()),
                        Toast.LENGTH_SHORT);
                itemRemovedToast.show();
            }
        });
        listView.setAdapter(this.monthListViewAdapter);

        this.registerForContextMenu(listView);

        setupCategoryListForMonth(Month.getCurrentMonth());
    }

    private void setupCategoryListForMonth(Month month)
    {
        UnitOfWork uow = this.persistenceContext.createUnitOfWork();
        MonthlyPlansModel model = uow.getMonthlyPlansRepository().findForMonth(month);

        if(model == null)
        {
            model = new MonthlyPlansModel();
            // todo: find good way to assign ids
            model.setId(1);
            model.setMonth(month);
            uow.getMonthlyPlansRepository().add(model);
        }

        this.setupHeaderForCategoryListForMonth(model);
        this.monthListViewAdapter.updateList(model.getCategories());

        uow.complete();
    }

    private void setupHeaderForCategoryListForMonth(MonthlyPlansModel monthlyPlans)
    {
        final LayoutInflater inflater = this.getLayoutInflater();
        // month text view
        final LinearLayout headerListItemMonth = (LinearLayout) this.findViewById(R.id.monthly_goals_header_list_item_month_panel);
        Spinner monthSpinner = (Spinner) headerListItemMonth.findViewById(R.id.monthly_goals_list_header_month_spinner);
        SpinnerHelper.setSelectedValue(monthSpinner, ResourcesHelper.toResourceStringId(monthlyPlans.getMonth()));

        final LinearLayout headerListItemDays = (LinearLayout) this.findViewById(R.id.monthly_goals_header_list_item_days_list);
        headerListItemDays.removeAllViews();

        //month days list
        List<Integer> listOfDays = CollectionUtils.createList(monthlyPlans.getMonth().getDaysCount(), new ElementCreator<Integer>() {

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
                headerListItemDays.addView(dayNumberCell);
            }
        });
    }
}
