package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.lists.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

/**
 * Created by moriasla on 28.02.2017.
 */

public class CategoryPlansSummaryRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryPlansSummaryRecyclerViewAdapter.CategoryPlansSummaryViewHolder> {

    public CategoryPlansSummaryRecyclerViewAdapter(Context context, LayoutInflater layoutInflater) {
        super(context, layoutInflater,
                new SortedList<>(CategoryPlansViewModel.class, new ComparatorSortedListCallback<>(new DefaultComparator<CategoryPlansViewModel>())));
    }
    public CategoryPlansSummaryRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, SortedList<CategoryPlansViewModel> sourceList) {
        super(context, layoutInflater, sourceList);
    }

    @Override
    public CategoryPlansSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.plans_category_summary_item_view, null);
        return new CategoryPlansSummaryViewHolder(view);
    }

    public  class CategoryPlansSummaryViewHolder extends BindableViewHolderBase<CategoryPlansViewModel> {

        public CategoryPlansSummaryViewHolder(View view) {
            super(view);
        }
    }
}
