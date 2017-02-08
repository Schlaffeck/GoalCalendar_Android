package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.view.lists.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.ViewHolderBase;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoryDailyPlansRecyclerViewAdapter extends RecyclerViewDataAdapter<CategoryModel, CategoryDailyPlansRecyclerViewAdapter.CategoryDailyPlansViewHolder> {

        private Set<CategoryModel> processedCategoriesSet;
        private MonthlyPlansModel monthlyPlans;

    public CategoryDailyPlansRecyclerViewAdapter(Context context, LayoutInflater layoutInflater)
    {
        super(context, layoutInflater, new SortedList<CategoryModel>(CategoryModel.class, new ComparatorSortedListCallback<CategoryModel>(new DefaultComparator<CategoryModel>())));
            this.processedCategoriesSet = new HashSet<>();
        }

    @Override
    public CategoryDailyPlansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = this.getLayoutInflater().inflate(R.layout.monthly_goals_daily_plans_list_item,null);
        convertView.setLongClickable(true);

        return new CategoryDailyPlansViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(CategoryDailyPlansViewHolder viewHolder, int position) {

        CategoryModel categoryModel = this.getItem(position);

        if(this.processedCategoriesSet.contains(categoryModel))
        {
            return;
        }

        DailyPlanRecyclerViewAdapter adapter = new DailyPlanRecyclerViewAdapter(
                this.getContext(),
                this.getLayoutInflater(),
                categoryModel.getDailyPlans());

        viewHolder.daysListGridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(viewHolder.isViewRendered()) {
            this.processedCategoriesSet.add(categoryModel);
        }

//        if(categoryModel == this.getItem(this.getItemCount()-1)) {
//            viewHolder.daysListGridView.setPadding(0, 0, 0,
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

    public class CategoryDailyPlansViewHolder extends ViewHolderBase<CategoryModel>
    {
        @BindView(R.id.monthly_goals_list_item_days_list)
        RecyclerView daysListGridView;

        CategoryDailyPlansViewHolder(
                View view)
        {
            super(view);
            this.daysListGridView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

}
