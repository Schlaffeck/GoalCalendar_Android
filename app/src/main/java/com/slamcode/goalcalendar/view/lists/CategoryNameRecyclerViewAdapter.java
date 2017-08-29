package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.BaseSourceChangeRequest;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.SortedListCallbackSet;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.lists.base.bindable.ObservableSortedList;
import com.slamcode.goalcalendar.view.lists.gestures.ItemDragCallback;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import java.util.Comparator;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoryNameRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryNameRecyclerViewAdapter.CategoryNameViewHolder>
                            implements ItemDragCallback.ItemDragMoveListener {

    private SortedList.Callback<CategoryPlansViewModel> recyclerViewCallback;

    public CategoryNameRecyclerViewAdapter(Context context, LayoutInflater layoutInflater, ObservableList<CategoryPlansViewModel> items)
        {
            super(context, layoutInflater, new ObservableSortedList<>(items, CategoryPlansViewModel.class,
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

            @Override
            public void onChanged(int position, int count) {
                super.onChanged(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                super.onRemoved(position, count);
            }

            @Override
            public void onChanged(int position, int count, Object payload) {
                super.onChanged(position, count, payload);
            }

            @Override
            public void onInserted(int position, int count) {
                super.onInserted(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                super.onMoved(fromPosition, toPosition);
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
    public CategoryNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = this.getLayoutInflater().inflate(R.layout.monthly_goals_category_list_item,null);

        convertView.setPadding(0, 0, 0,
                viewType == ITEM_VIEM_TYPE_LAST_ITEM ?
                        convertView.getResources().getDimensionPixelSize(R.dimen.monthly_goals_category_listView_lastItem_paddingBottom) : 0
        );

        return new CategoryNameViewHolder(convertView, null);
    }

    @Override
    public void onItemDragMoved(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    public class CategoryNameViewHolder extends BindableViewHolderBase<CategoryPlansViewModel>
    {
        CategoryNameViewHolder(
                View view,
                CategoryPlansViewModel model)
        {
            super(view, model);
        }

        public void requestItemRemove()
        {
            this.getModelObject().notifySourceChangeRequested(new BaseSourceChangeRequest(CategoryPlansViewModel.REQUEST_REMOVE_ITEM));
        }

        public void requestItemModify()
        {
            this.getModelObject().notifySourceChangeRequested(new BaseSourceChangeRequest(CategoryPlansViewModel.REQUEST_MODIFY_ITEM));
        }

        @Override
        public void bindToModel(CategoryPlansViewModel modelObject) {
            super.bindToModel(modelObject);
            this.getBinding().setVariable(BR.presenter,  this);
        }
    }

}
