package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;

/**
 * Created by moriasla on 27.02.2017.
 */

public class CategoryPlansRecyclerViewAdapter extends RecyclerViewDataAdapter<PlansSummaryCalculator.CategoryPlansSummary, CategoryPlansRecyclerViewAdapter.CategoryPlansSummaryViewHolder> {

    protected CategoryPlansRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, SortedList<PlansSummaryCalculator.CategoryPlansSummary> sourceList) {
        super(context, layoutInflater, sourceList);
    }

    @Override
    public CategoryPlansSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = this.getLayoutInflater().inflate(R.layout.plans_category_summary_item_view,null);

        return new CategoryPlansSummaryViewHolder(view);
    }

    public class CategoryPlansSummaryViewHolder extends ViewHolderBase<PlansSummaryCalculator.CategoryPlansSummary>{

        private final ViewDataBinding binding;

        public CategoryPlansSummaryViewHolder(View view) {
            super(view);
            this.binding = DataBindingUtil.bind(view);
        }

        @Override
        public void bindToModel(PlansSummaryCalculator.CategoryPlansSummary modelObject) {
            super.bindToModel(modelObject);
        }
    }
}
