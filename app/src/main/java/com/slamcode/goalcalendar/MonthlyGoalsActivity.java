package com.slamcode.goalcalendar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.data.*;
import com.slamcode.goalcalendar.data.inmemory.InMemoryCategoriesRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.view.AddEditCategoryDialog;
import com.slamcode.goalcalendar.view.CategoriesListViewAdapter;
import com.slamcode.goalcalendar.view.lists.ListViewDataAdapter;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IteratorUtils;

import java.util.List;

public class MonthlyGoalsActivity extends AppCompatActivity {

    private CategoriesListViewAdapter monthListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.slamcode.goalcalendar.R.layout.monthly_goals_activity);
        Toolbar toolbar = (Toolbar) findViewById(com.slamcode.goalcalendar.R.id.toolbar);
        setSupportActionBar(toolbar);

        setupMonthlyPlanningCategoryList();
        setupFloatingButtonAction();
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
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        CategoryModel model = (CategoryModel)this.monthListViewAdapter.getItem(info.position);
        this.showAddEditCategoryDialog(model);
        return true;
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

        this.monthListViewAdapter = new CategoriesListViewAdapter(this, getLayoutInflater());
        final ListView listView = (ListView) this.findViewById(R.id.monthly_goals_listview);
        this.monthListViewAdapter.addItemSourceChangedEventListener(new ListViewDataAdapter.ItemsSourceChangedEventListener() {
            @Override
            public void onNewItemAdded(int itemPosition) {
                listView.smoothScrollToPosition(itemPosition);
            }

            @Override
            public void onItemModified(int itemPosition) {
                listView.smoothScrollToPosition(itemPosition);
            }
        });
        listView.setAdapter(this.monthListViewAdapter);

        this.registerForContextMenu(listView);

        this.setupCategoryListForMonth(Month.getCurrentMonth());
    }

    private void setupCategoryListForMonth(Month month)
    {
        // todo: move repo to di container
        CategoryRepository repository = InMemoryCategoriesRepository.buildDefaultRepository();

        this.setupHeaderForCategoryListForMonth(month);
        this.monthListViewAdapter.updateList(repository.findForMonth(month));
    }

    private void setupHeaderForCategoryListForMonth(Month month)
    {
        final LayoutInflater inflater = this.getLayoutInflater();
        // month text view
        final LinearLayout header = (LinearLayout) this.findViewById(R.id.monthly_goals_list_header);
        TextView monthName = (TextView) header.findViewById(R.id.monthly_goals_list_header_month_text);
        monthName.setText(month.name());

        //month days list
        List<Integer> listOfDays = CollectionUtils.createList(31, new ElementCreator<Integer>() {

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
                header.addView(dayNumberCell);
            }
        });
    }
}
