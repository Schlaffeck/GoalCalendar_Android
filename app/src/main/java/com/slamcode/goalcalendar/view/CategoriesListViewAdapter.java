package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.lists.ListViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;
import com.slamcode.goalcalendar.data.model.*;

import java.util.*;

import butterknife.BindView;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoriesListViewAdapter extends ListViewDataAdapter<CategoryModel, CategoriesListViewAdapter.CategoryViewHolder> {

        private Set<CategoryModel> processedCategoriesSet;

        public CategoriesListViewAdapter(Context context, LayoutInflater layoutInflater)
        {
            super(context, layoutInflater);
            this.processedCategoriesSet = new HashSet<>();
        }

    @Override
    protected CategoryViewHolder getNewViewHolder(View convertView, long id) {
        convertView = this.getLayoutInflater().inflate(R.layout.monthly_goals_category_list_item,null);
        convertView.setLongClickable(true);

        return new CategoryViewHolder(
                convertView,
                id);
    }

    @Override
    protected void fillListElementView(CategoryModel monthlyGoals, final CategoryViewHolder viewHolder)
    {
        if(this.processedCategoriesSet.contains(monthlyGoals))
        {
            return;
        }

        // put view holder to array so it can be modified from inner class
        final CategoryViewHolder[] innerViewHolder = new CategoryViewHolder[] { viewHolder };

        innerViewHolder[0].categoryNameTextView.setText(monthlyGoals.getName());
        innerViewHolder[0].frequencyTextView
                .setText(String.format("%s x %s", monthlyGoals.getFrequencyValue(), monthlyGoals.getPeriod()));

        for (DailyPlanModel dailyPlan : monthlyGoals.getDailyPlans())
        {
            View elem = getDayPlanStatusView(dailyPlan);
            innerViewHolder[0].daysListGridView.addView(elem);
        }

        if(innerViewHolder[0].isViewVisible()) {
            this.processedCategoriesSet.add(monthlyGoals);
        }
    }

    @Override
    public void updateList(List<CategoryModel> list)
    {
        this.processedCategoriesSet.clear();
        super.updateList(list);
    }

    public class CategoryViewHolder extends ViewHolderBase<CategoryModel>
    {
        @BindView(R.id.monthly_goals_list_item_category_name)
        TextView categoryNameTextView;

        @BindView(R.id.monthly_goals_list_item_frequency)
        TextView frequencyTextView;

        @BindView(R.id.monthly_goals_list_item_days_list)
        LinearLayout daysListGridView;

        CategoryViewHolder(
                View view,
                long id)
        {
            super(view, id);
        }
    }

    private View getDayPlanStatusView(final DailyPlanModel planStatus)
    {
        View layout = this.getLayoutInflater().inflate(R.layout.monthly_goals_plan_status_cell, null);
        Button button = (Button)layout.findViewById(R.id.plan_status_list_item_view_button);
        button.setText(planStatus.getStatus().toString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button  btn = (Button) view;
                planStatus.setStatus(planStatus.getStatus().nextStatus());
                btn.setText(planStatus.getStatus().toString());
            }
        });

        return layout;
    }
}
