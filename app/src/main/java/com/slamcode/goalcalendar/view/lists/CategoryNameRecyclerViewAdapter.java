package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.BaseSourceChangeRequest;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.data.model.*;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.lists.base.bindable.ObservableSortedList;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoryNameRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryNameRecyclerViewAdapter.CategoryNameViewHolder> {

    public static final int CONTEXT_MENU_DELETE_ITEM_ID = 222;
    public static final int CONTEXT_MENU_EDIT_ITEM_ID = 221;

        private MonthlyPlansModel monthlyPlans;

    public CategoryNameRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, ObservableList<CategoryPlansViewModel> items)
        {
            super(context, layoutInflater, new ObservableSortedList<>(
                    items,
                    CategoryPlansViewModel.class,
                    new ComparatorSortedListCallback<>(new DefaultComparator<CategoryPlansViewModel>())));
        }

    @Override
    public CategoryNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = this.getLayoutInflater().inflate(R.layout.monthly_goals_category_list_item,null);
        convertView.setLongClickable(true);

        convertView.setPadding(0, 0, 0,
                viewType == ITEM_VIEM_TYPE_LAST_ITEM ?
                        convertView.getResources().getDimensionPixelSize(R.dimen.monthly_goals_category_listView_lastItem_paddingBottom) : 0
        );

        return new CategoryNameViewHolder(convertView, null);
    }

    public class CategoryNameViewHolder extends BindableViewHolderBase<CategoryPlansViewModel> implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener
    {
        CategoryNameViewHolder(
                View view,
                CategoryPlansViewModel model)
        {
            super(view, model);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderTitle(this.getModelObject().getName());
            menu.add(0, CONTEXT_MENU_EDIT_ITEM_ID, 0, R.string.context_menu_edit_item)
                .setOnMenuItemClickListener(this);
            menu.add(0, CONTEXT_MENU_DELETE_ITEM_ID, 0, R.string.context_menu_delete_item)
                .setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch(item.getItemId()) {
                case CONTEXT_MENU_DELETE_ITEM_ID:
                    this.getModelObject().notifySourceChangeRequested(new BaseSourceChangeRequest(CategoryPlansViewModel.REQUEST_REMOVE_ITEM));
                    return true;
                case CONTEXT_MENU_EDIT_ITEM_ID:
                    this.getModelObject().notifySourceChangeRequested(new BaseSourceChangeRequest(CategoryPlansViewModel.REQUEST_MODIFY_ITEM));
                    return true;
                default:
                    return false;
            }
        }
    }

}
