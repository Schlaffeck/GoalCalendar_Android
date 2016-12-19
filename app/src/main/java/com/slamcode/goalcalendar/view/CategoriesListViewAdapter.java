package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.base.ListViewDataAdapter;
import com.slamcode.goalcalendar.view.base.ViewHolderBase;
import com.slamcode.goalcalendar.data.model.*;

import java.util.*;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoriesListViewAdapter extends ListViewDataAdapter<CategoryModel, CategoriesListViewAdapter.CategoryViewHolder> {

        private List<CategoryModel> list;

        public CategoriesListViewAdapter(Context context, LayoutInflater layoutInflater)
        {
            super(context, layoutInflater);
            this.list = new ArrayList<CategoryModel>();
        }

    @Override
    protected CategoryViewHolder getNewViewHolder(View convertView) {
        convertView = this.getLayoutInflater().inflate(R.layout.monthly_goals_category_list_item,null);

        return new CategoryViewHolder(
                convertView,
                (TextView)convertView.findViewById(R.id.monthly_goals_list_item_category_name),
                (TextView)convertView.findViewById(R.id.monthly_goals_list_item_frequency),
                (LinearLayout)convertView.findViewById(R.id.monthly_goals_list_item_days_list));
    }

    @Override
    protected void fillListElementView(CategoryModel monthlyGoals, final CategoryViewHolder viewHolder)
    {
        // put view holder to array so it can be modified from inner class
        final CategoryViewHolder[] innerViewHolder = new CategoryViewHolder[] { viewHolder };

        innerViewHolder[0].categoryNameTextView.setText(monthlyGoals.getName());
        innerViewHolder[0].frequencyTextView.setText(monthlyGoals.getFrequency().toString());

        for (DailyPlanModel dailyPlan : monthlyGoals.getDailyPlans())
        {
            View elem = getDayPlanStatusView(dailyPlan);
            innerViewHolder[0].daysListGridView.addView(elem);
        }
    }

    public class CategoryViewHolder extends ViewHolderBase<CategoryModel>
    {
        private CategoryModel baseObject;
        private TextView categoryNameTextView;
        private TextView frequencyTextView;
        private LinearLayout daysListGridView;

        CategoryViewHolder(
                View view,
                TextView categoryNameTextView,
                   TextView frequencyTextView,
                   LinearLayout daysListGridView)
        {
            super(view);
            this.categoryNameTextView = categoryNameTextView;
            this.frequencyTextView = frequencyTextView;
            this.daysListGridView = daysListGridView;
        }

        public CategoryModel getBaseObject() {
            return baseObject;
        }

        public void setBaseObject(CategoryModel baseObject) {
            this.baseObject = baseObject;
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
