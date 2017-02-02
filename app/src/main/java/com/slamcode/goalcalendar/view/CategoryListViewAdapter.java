package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.view.controls.GoalPlanStatusButton;
import com.slamcode.goalcalendar.view.lists.ListViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;
import com.slamcode.goalcalendar.data.model.*;
import com.slamcode.goalcalendar.view.utils.ColorsHelper;

import java.util.*;

import butterknife.BindView;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoryListViewAdapter extends ListViewDataAdapter<CategoryModel, CategoryListViewAdapter.CategoryViewHolder> {

        private Set<CategoryModel> processedCategoriesSet;
        private Map<View, View> associatedParentViews;
        private MonthlyPlansModel monthlyPlans;

    public CategoryListViewAdapter(Context context, LayoutInflater layoutInflater)
        {
            super(context, layoutInflater);
            this.associatedParentViews = new HashMap<>();
            this.processedCategoriesSet = new HashSet<>();
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View baseView = super.getView(position, convertView, parent);

        View resultView = null;
        if(parent.getId() == R.id.monthly_goals_listview)
        {
            resultView = baseView.findViewById(R.id.monthly_goals_list_item_category_panel);
        }
        else if(parent.getId() == R.id.monthly_goals_dailyplans_listview)
        {
            resultView = baseView.findViewById(R.id.monthly_goals_list_item_days_list);
        }

        if(resultView != null)
        {
            resultView.setTag(baseView.getTag());
            if(!this.associatedParentViews.containsKey(resultView))
            {
                this.associatedParentViews.put(resultView, baseView);
            }
            return resultView;
        }

        return baseView;
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
                .setText(String.format("%s x %s",
                        monthlyGoals.getFrequencyValue(),
                        ResourcesHelper.toResourceString(this.getContext(), monthlyGoals.getPeriod())));

        innerViewHolder[0].daysListGridView.removeAllViews();
        for (DailyPlanModel dailyPlan : monthlyGoals.getDailyPlans())
        {
            View elem = getDayPlanStatusView(monthlyGoals, dailyPlan);
            innerViewHolder[0].daysListGridView.addView(elem);
        }

        if(innerViewHolder[0].isViewRendered()) {
            this.processedCategoriesSet.add(monthlyGoals);
        }

        if(monthlyGoals == this.getItem(this.getCount()-1)) {
            viewHolder.daysListGridView.setPadding(0, 0, 0,
                    viewHolder.getView().getResources().getDimensionPixelSize(R.dimen.monthly_goals_category_listView_lastItem_paddingBottom));
            viewHolder.categoryPanel.setPadding(0, 0, 0,
                    viewHolder.getView().getResources().getDimensionPixelSize(R.dimen.monthly_goals_category_listView_lastItem_paddingBottom));
        }
    }

    public void updateMonthlyPlans(MonthlyPlansModel monthlyPlansModel)
    {
        this.processedCategoriesSet.clear();
        this.monthlyPlans = monthlyPlansModel;
        this.setList(monthlyPlansModel.getCategories());
        this.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        // sort before update
        Collections.sort(this.getList(), new CategoryListItemComparator());
        super.notifyDataSetChanged();
    }

    @Override
    public void notifyItemModified(int position)
    {
        CategoryModel item = this.getItem(position);
        if(item != null)
        {
            this.processedCategoriesSet.remove(item);
        }
        super.notifyItemModified(position);
    }

    public class CategoryViewHolder extends ViewHolderBase<CategoryModel>
    {
        @BindView(R.id.monthly_goals_list_item_category_panel)
        LinearLayout categoryPanel;

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

    private View getDayPlanStatusView(CategoryModel monthlyGoals, final DailyPlanModel planStatus)
    {
        View layout = this.getLayoutInflater().inflate(R.layout.monthly_goals_plan_status_cell, null);
        GoalPlanStatusButton button = (GoalPlanStatusButton) layout.findViewById(R.id.plan_status_list_item_view_button);
        button.setStatus(planStatus.getStatus());
        button.addOnStateChangedListener(new GoalPlanStatusButton.OnStateChangedListener() {
            @Override
            public void onStateChanged(PlanStatus newState) {
                planStatus.setStatus(newState);
            }
        });

        if(this.isCurrentDate(monthlyGoals, planStatus))
        {
            ColorsHelper.setSecondAccentBackgroundColor(layout);
        }

        return layout;
    }

    private boolean isCurrentDate(CategoryModel category, DailyPlanModel dailyPlanModel)
    {
        int dayNumber = dailyPlanModel.getDayNumber();

        if(dayNumber <= 0 && category.getDailyPlans().contains(dailyPlanModel))
        {
            dayNumber = category.getDailyPlans().indexOf(dailyPlanModel)+1;
        }

        int year = this.monthlyPlans.getYear() > 0 ?
                    this.monthlyPlans.getYear() : DateTimeHelper.getCurrentYear();

        return DateTimeHelper.isCurrentDate(
                year,
                this.monthlyPlans.getMonth().getNumValue(),
                dayNumber);
    }

    public static class CategoryListItemComparator implements Comparator<CategoryModel>
    {
        @Override
        public int compare(CategoryModel first, CategoryModel second) {
            if(first == null && second == null)
                return 0;

            if(first == null)
                return 1;

            if(second == null)
                return -1;

            return first.compareTo(second);
        }
    }
}
