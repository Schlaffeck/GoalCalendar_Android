package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.view.lists.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;
import com.slamcode.goalcalendar.data.model.*;

import java.util.*;

import butterknife.BindView;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoryNameRecyclerViewAdapter extends RecyclerViewDataAdapter<CategoryModel, CategoryNameRecyclerViewAdapter.CategoryNameViewHolder> {

        private Set<CategoryModel> processedCategoriesSet;
        private MonthlyPlansModel monthlyPlans;

    public CategoryNameRecyclerViewAdapter(Context context, LayoutInflater layoutInflater)
        {
            super(context, layoutInflater, new SortedList<CategoryModel>(CategoryModel.class, new ComparatorSortedListCallback<CategoryModel>(new DefaultComparator<CategoryModel>())));
            this.processedCategoriesSet = new HashSet<>();
        }

    @Override
    public CategoryNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = this.getLayoutInflater().inflate(R.layout.monthly_goals_category_list_item,null);
        convertView.setLongClickable(true);

        return new CategoryNameViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(CategoryNameViewHolder viewHolder, int position) {

        CategoryModel monthlyGoals = this.getItem(position);

        if(this.processedCategoriesSet.contains(monthlyGoals))
        {
            return;
        }

        // put view holder to array so it can be modified from inner class
        final CategoryNameViewHolder[] innerViewHolder = new CategoryNameViewHolder[] { viewHolder };

        innerViewHolder[0].categoryNameTextView.setText(monthlyGoals.getName());
        innerViewHolder[0].frequencyTextView
                .setText(String.format("%s x %s",
                        monthlyGoals.getFrequencyValue(),
                        ResourcesHelper.toResourceString(this.getContext(), monthlyGoals.getPeriod())));

        if(innerViewHolder[0].isViewRendered()) {
            this.processedCategoriesSet.add(monthlyGoals);
        }

//        if(monthlyGoals == this.getItem(this.getItemCount()-1)) {
//            viewHolder.categoryPanel.setPadding(0, 0, 0,
//                    viewHolder.getView().getResources().getDimensionPixelSize(R.dimen.monthly_goals_category_listView_lastItem_paddingBottom));
//        }
    }

    public void updateMonthlyPlans(MonthlyPlansModel monthlyPlansModel)
    {
        this.processedCategoriesSet.clear();
        this.monthlyPlans = monthlyPlansModel;
        if(monthlyPlansModel != null)
            this.updateSourceCollection(monthlyPlansModel.getCategories());
        else this.updateSourceCollection(CollectionUtils.<CategoryModel>emptyList());
        this.notifyDataSetChanged();
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

    public class CategoryNameViewHolder extends ViewHolderBase<CategoryModel>
    {
        @BindView(R.id.monthly_goals_list_item_category_panel)
        LinearLayout categoryPanel;

        @BindView(R.id.monthly_goals_list_item_category_name)
        TextView categoryNameTextView;

        @BindView(R.id.monthly_goals_list_item_frequency)
        TextView frequencyTextView;

        CategoryNameViewHolder(
                View view)
        {
            super(view);
        }
    }

}
