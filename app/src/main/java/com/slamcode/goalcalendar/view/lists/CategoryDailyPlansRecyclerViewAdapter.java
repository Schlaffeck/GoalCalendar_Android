package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.ViewHolderBase;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.lists.base.bindable.ObservableSortedList;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import butterknife.BindView;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoryDailyPlansRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryDailyPlansRecyclerViewAdapter.CategoryDailyPlansViewHolder> {

        private YearMonthPair yearMonthPair;

    public CategoryDailyPlansRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, YearMonthPair yearMonthPair, ObservableList<CategoryPlansViewModel> items)
    {
        super(context, layoutInflater, new ObservableSortedList<>(items, CategoryPlansViewModel.class, new ComparatorSortedListCallback<>(new DefaultComparator<CategoryPlansViewModel>())));

        this.yearMonthPair = yearMonthPair;
    }


    @Override
    public CategoryDailyPlansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = this.getLayoutInflater().inflate(R.layout.monthly_goals_daily_plans_list_item,null);
        convertView.setLongClickable(true);

        convertView.setPadding(0, 0, 0,
                viewType == ITEM_VIEM_TYPE_LAST_ITEM ?
                        convertView.getResources().getDimensionPixelSize(R.dimen.monthly_goals_category_listView_lastItem_paddingBottom) : 0
        );
        return new CategoryDailyPlansViewHolder(convertView);
    }

    public class CategoryDailyPlansViewHolder extends BindableViewHolderBase<CategoryPlansViewModel>
    {
        @BindView(R.id.monthly_goals_list_item_days_list)
        RecyclerView daysListGridView;

        CategoryDailyPlansViewHolder(
                View view)
        {
            super(view);
            this.daysListGridView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        @Override
        public void bindToModel(CategoryPlansViewModel modelObject) {
            super.bindToModel(modelObject);

            DailyPlanRecyclerViewAdapter adapter = new DailyPlanRecyclerViewAdapter(
                    getView().getContext(),
                    getLayoutInflater(),
                    yearMonthPair,
                    modelObject.getDailyPlansList());

            this.daysListGridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
