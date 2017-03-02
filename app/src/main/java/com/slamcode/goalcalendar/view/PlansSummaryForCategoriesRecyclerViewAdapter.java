package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.BR;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.lists.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;
import com.slamcode.goalcalendar.viewmodels.PlansSummaryForCategoryViewModel;

/**
 * Created by moriasla on 28.02.2017.
 */

public class PlansSummaryForCategoriesRecyclerViewAdapter extends RecyclerViewDataAdapter<PlansSummaryForCategoryViewModel, PlansSummaryForCategoriesRecyclerViewAdapter.CategoryPlansSummaryViewHolder> {

    public PlansSummaryForCategoriesRecyclerViewAdapter(Context context, LayoutInflater layoutInflater) {
        super(context, layoutInflater,
                new SortedList<>(PlansSummaryForCategoryViewModel.class, new ComparatorSortedListCallback<>(new DefaultComparator<PlansSummaryForCategoryViewModel>())));
    }
    public PlansSummaryForCategoriesRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, SortedList<PlansSummaryForCategoryViewModel> sourceList) {
        super(context, layoutInflater, sourceList);
    }

    @Override
    public CategoryPlansSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.plans_category_summary_item_view, null);
        return new CategoryPlansSummaryViewHolder(view);
    }

    public  class CategoryPlansSummaryViewHolder extends ViewHolderBase<PlansSummaryForCategoryViewModel>{

        private final ViewDataBinding binding;

        public CategoryPlansSummaryViewHolder(View view) {
            super(view);
            this.binding = DataBindingUtil.bind(view);
        }

        @Override
        public void bindToModel(PlansSummaryForCategoryViewModel modelObject) {
            super.bindToModel(modelObject);
            this.binding.setVariable(BR.vm, modelObject);
        }
    }
}
