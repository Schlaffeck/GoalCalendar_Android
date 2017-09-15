package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangeListenersRegistry;
import com.slamcode.goalcalendar.view.BaseSourceChangeRequest;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.SortedListCallbackSet;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.lists.base.bindable.ObservableSortedList;
import com.slamcode.goalcalendar.view.lists.gestures.ItemDragCallback;
import com.slamcode.goalcalendar.view.utils.ViewBinder;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;
import com.slamcode.goalcalendar.viewmodels.DailyPlansViewModel;

import java.util.ArrayList;
import java.util.Collection;

public class CategoryPlansRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryPlansRecyclerViewAdapter.CategoryPlansViewHolder>
        implements ItemDragCallback.ItemDragMoveListener{

    private final DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry;
    private YearMonthPair yearMonthPair;
    private ItemDragCallback itemDragCallback;
    private CategoryListChangedCallback listChangedCallback = new CategoryListChangedCallback();
    private RecyclerView attachedRecyclerView;

    public CategoryPlansRecyclerViewAdapter(DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry,
                                            YearMonthPair yearMonthPair,
                                            ObservableList<CategoryPlansViewModel> items)
    {
        super(items);
        this.dateTimeChangeListenersRegistry = dateTimeChangeListenersRegistry;
        this.yearMonthPair = yearMonthPair;
        this.getSourceList().addOnListChangedCallback(this.listChangedCallback);
    }

    public void setYearMonthPair(YearMonthPair yearMonthPair) {
        this.yearMonthPair = yearMonthPair;
    }

    @Override
    public void updateSourceCollection(Collection<CategoryPlansViewModel> newSourceCollection) {
        this.getSourceList().removeOnListChangedCallback(this.listChangedCallback);
        super.updateSourceCollection(newSourceCollection);
        this.getSourceList().addOnListChangedCallback(this.listChangedCallback);
    }

    @Override
    public CategoryPlansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_goals_category_list_item,null);
        convertView.setLongClickable(false);


        final View categoryView = convertView.findViewById(R.id.monthly_goals_list_item_category_panel);
        categoryView.setLongClickable(true);

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                itemDragCallback.setDraggingEnabled(v == categoryView && v.isLongClickable()
                        && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE));
                return false;
            }
        };

        convertView.setOnTouchListener(onTouchListener);
        categoryView.setOnTouchListener(onTouchListener);

        return new CategoryPlansViewHolder(convertView, null);
    }

    @Override
    public void onItemDragMoved(int fromPosition, int toPosition) {
        this.listChangedCallback.setEnabled(false);
        notifyItemMoved(fromPosition, toPosition);
        ObservableListUtils.moveItem(this.getSourceList(), fromPosition, toPosition);
        this.listChangedCallback.setEnabled(true);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.attachedRecyclerView = recyclerView;
        if(this.itemDragCallback != null)
        {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this.itemDragCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    public void setUpItemDragging(ItemDragCallback itemDragCallback)
    {
        this.itemDragCallback = itemDragCallback;
        this.itemDragCallback.addOnItemGestureListener(this);
    }

    public class CategoryPlansViewHolder extends BindableViewHolderBase<CategoryPlansViewModel>
    {
        @ViewReference(R.id.monthly_goals_list_item_days_list)
        RecyclerView daysListListRecyclerView;

        CategoryPlansViewHolder(View view, CategoryPlansViewModel model)
        {
            super(view, model);
            this.daysListListRecyclerView = ViewBinder.findView(view, R.id.monthly_goals_list_item_days_list);
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
        public void bindToModel(final CategoryPlansViewModel modelObject) {
            super.bindToModel(modelObject);

            this.getBinding().setVariable(BR.presenter, this);

            final DailyPlanRecyclerViewAdapter adapter = new DailyPlanRecyclerViewAdapter(
                    dateTimeChangeListenersRegistry,
                    yearMonthPair,
                    new ArrayList<DailyPlansViewModel>());

            this.daysListListRecyclerView.setAdapter(adapter);
            adapter.updateSourceCollection(modelObject.getDailyPlansList());
        }
    }

    private class CategoryListChangedCallback extends ObservableList.OnListChangedCallback<ObservableList<CategoryPlansViewModel>> {

        private boolean enabled = true;

        @Override
        public void onChanged(ObservableList<CategoryPlansViewModel> sender) {
            if(!this.enabled)
                return;
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<CategoryPlansViewModel> sender, int positionStart, int itemCount) {
            if(!this.enabled)
                return;
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<CategoryPlansViewModel> sender, int positionStart, int itemCount) {
            if(!this.enabled)
                return;
            notifyItemRangeInserted(positionStart, itemCount);
            if(attachedRecyclerView != null)
                attachedRecyclerView.smoothScrollToPosition(positionStart);
        }

        @Override
        public void onItemRangeMoved(ObservableList<CategoryPlansViewModel> sender, int fromPosition, int toPosition, int itemCount) {
            if(!this.enabled)
                return;
        }

        @Override
        public void onItemRangeRemoved(ObservableList<CategoryPlansViewModel> sender, int positionStart, int itemCount) {
            if(!this.enabled)
                return;
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
