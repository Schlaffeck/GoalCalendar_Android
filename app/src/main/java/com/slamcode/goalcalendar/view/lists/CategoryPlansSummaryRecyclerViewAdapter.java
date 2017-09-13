package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.SortedListCallbackSet;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.lists.base.bindable.ObservableSortedList;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import java.util.Collection;
import java.util.Comparator;

/**
 * Created by moriasla on 28.02.2017.
 */

public class CategoryPlansSummaryRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryPlansSummaryRecyclerViewAdapter.CategoryPlansSummaryViewHolder> {

    private ObservableList.OnListChangedCallback<ObservableList<CategoryPlansViewModel>> listChangedCallback = new CategoryListChangedCallback();

    public CategoryPlansSummaryRecyclerViewAdapter()
    {
        super(new ObservableArrayList<CategoryPlansViewModel>());
        this.getSourceList().addOnListChangedCallback(this.listChangedCallback);
    }

    @Override
    public CategoryPlansSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plans_category_summary_item_view, null);
        return new CategoryPlansSummaryViewHolder(view);
    }

    @Override
    public void updateSourceCollection(Collection<CategoryPlansViewModel> newSourceCollection) {
        this.getSourceList().removeOnListChangedCallback(this.listChangedCallback);
        super.updateSourceCollection(newSourceCollection);
        this.getSourceList().addOnListChangedCallback(this.listChangedCallback);
    }

    public class CategoryPlansSummaryViewHolder extends BindableViewHolderBase<CategoryPlansViewModel> {

        public CategoryPlansSummaryViewHolder(View view) {
            super(view);
        }
    }

    private class CategoryListChangedCallback extends ObservableList.OnListChangedCallback<ObservableList<CategoryPlansViewModel>> {

        @Override
        public void onChanged(ObservableList<CategoryPlansViewModel> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<CategoryPlansViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<CategoryPlansViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<CategoryPlansViewModel> sender, int fromPosition, int toPosition, int itemCount) {
        }

        @Override
        public void onItemRangeRemoved(ObservableList<CategoryPlansViewModel> sender, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }
}
