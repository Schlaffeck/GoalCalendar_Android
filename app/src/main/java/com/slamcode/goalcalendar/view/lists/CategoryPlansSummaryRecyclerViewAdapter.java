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

import java.util.Comparator;

/**
 * Created by moriasla on 28.02.2017.
 */

public class CategoryPlansSummaryRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryPlansSummaryRecyclerViewAdapter.CategoryPlansSummaryViewHolder> {

    private SortedListAdapterCallback<CategoryPlansViewModel> recyclerViewCallback;

    public CategoryPlansSummaryRecyclerViewAdapter(Context context, LayoutInflater layoutInflater)
    {
        super(context, layoutInflater,
                new ObservableSortedList<>(new ObservableArrayList<CategoryPlansViewModel>(), CategoryPlansViewModel.class,
                        new SortedListCallbackSet<>(new DefaultComparator<CategoryPlansViewModel>())));

        this.getSourceList().getCallbackSet().addCallback(new ComparatorSortedListCallback<>(new DefaultComparator<CategoryPlansViewModel>()));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // assign additional callback
        this.getSourceList().getCallbackSet().addCallback(this.recyclerViewCallback = new SortedListAdapterCallback<CategoryPlansViewModel>(this) {

            private Comparator<CategoryPlansViewModel> comparator = new DefaultComparator<>();
            @Override
            public int compare(CategoryPlansViewModel o1, CategoryPlansViewModel o2) {
                return this.comparator.compare(o1, o2);
            }

            @Override
            public boolean areContentsTheSame(CategoryPlansViewModel oldItem, CategoryPlansViewModel newItem) {
                return this.comparator.compare(oldItem, newItem) == 0;
            }

            @Override
            public boolean areItemsTheSame(CategoryPlansViewModel item1, CategoryPlansViewModel item2) {
                return item1 == item2;
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        // remove callback
        this.getSourceList().getCallbackSet().removeCallback(this.recyclerViewCallback);
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public CategoryPlansSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.plans_category_summary_item_view, null);
        return new CategoryPlansSummaryViewHolder(view);
    }

    public class CategoryPlansSummaryViewHolder extends BindableViewHolderBase<CategoryPlansViewModel> {

        public CategoryPlansSummaryViewHolder(View view) {
            super(view);
        }
    }
}
