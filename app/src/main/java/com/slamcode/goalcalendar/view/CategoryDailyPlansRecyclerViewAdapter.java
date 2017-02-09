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

public final class CategoryDailyPlansRecyclerViewAdapter extends RecyclerViewDataAdapter<CategoryModel, CategoryDailyPlansRecyclerViewAdapter.CategoryDailyPlansViewHolder> {

        private MonthlyPlansModel monthlyPlans;

    public CategoryDailyPlansRecyclerViewAdapter(Context context, LayoutInflater layoutInflater)
    {
        this(context, layoutInflater, null);
    }

    public CategoryDailyPlansRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, MonthlyPlansModel monthlyPlans)
    {
        super(context, layoutInflater, new SortedList<>(CategoryModel.class, new ComparatorSortedListCallback<>(new DefaultComparator<CategoryModel>())));

        this.monthlyPlans = monthlyPlans;
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

    public void updateMonthlyPlans(MonthlyPlansModel monthlyPlansModel)
    {
        this.monthlyPlans = monthlyPlansModel;
        if(monthlyPlansModel != null)
            this.updateSourceCollection(monthlyPlansModel.getCategories());
        else this.updateSourceCollection(CollectionUtils.<CategoryModel>emptyList());
        this.notifyDataSetChanged();
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

        @Override
        public void bindToModel(CategoryModel modelObject) {
            super.bindToModel(modelObject);

            DailyPlanRecyclerViewAdapter adapter = new DailyPlanRecyclerViewAdapter(
                    getView().getContext(),
                    getLayoutInflater(),
                    monthlyPlans,
                    modelObject.getDailyPlans());

            this.daysListGridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
