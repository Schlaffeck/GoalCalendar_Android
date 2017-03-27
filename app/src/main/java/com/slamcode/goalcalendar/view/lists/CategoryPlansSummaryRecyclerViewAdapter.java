package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.lists.base.bindable.ObservableSortedList;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

/**
 * Created by moriasla on 28.02.2017.
 */

public class CategoryPlansSummaryRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryPlansSummaryRecyclerViewAdapter.CategoryPlansSummaryViewHolder> {

    public CategoryPlansSummaryRecyclerViewAdapter(Context context, LayoutInflater layoutInflater) {
        super(context, layoutInflater,
                new ObservableSortedList<>(new ObservableArrayList<CategoryPlansViewModel>(), CategoryPlansViewModel.class, new ComparatorSortedListCallback<>(new DefaultComparator<CategoryPlansViewModel>())));
    }

    public CategoryPlansSummaryRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, ObservableSortedList<CategoryPlansViewModel> sourceList) {
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
