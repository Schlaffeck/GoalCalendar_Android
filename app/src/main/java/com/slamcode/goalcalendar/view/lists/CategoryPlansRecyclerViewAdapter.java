package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
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
import java.util.Comparator;

public class CategoryPlansRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<CategoryPlansViewModel, CategoryPlansRecyclerViewAdapter.CategoryPlansViewHolder>
        implements ItemDragCallback.ItemDragMoveListener{

    private final DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry;
    private YearMonthPair yearMonthPair;

    private SortedList.Callback<CategoryPlansViewModel> recyclerViewCallback;

    public CategoryPlansRecyclerViewAdapter(Context context,
                                            LayoutInflater layoutInflater,
                                            DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry,
                                            YearMonthPair yearMonthPair,
                                            ObservableList<CategoryPlansViewModel> items)
    {
        super(context, layoutInflater, new ObservableSortedList<>(items, CategoryPlansViewModel.class,
                new SortedListCallbackSet<>(new DefaultComparator<CategoryPlansViewModel>())));
        this.dateTimeChangeListenersRegistry = dateTimeChangeListenersRegistry;
        this.yearMonthPair = yearMonthPair;
        this.getSourceList().getCallbackSet().addCallback(new ComparatorSortedListCallback<>(new DefaultComparator<CategoryPlansViewModel>()));
    }

    public void setYearMonthPair(YearMonthPair yearMonthPair) {
        this.yearMonthPair = yearMonthPair;
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
    public CategoryPlansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = this.getLayoutInflater().inflate(R.layout.monthly_goals_category_list_item,null);

        convertView.setPadding(0, 0, 0,
                viewType == ITEM_VIEM_TYPE_LAST_ITEM ?
                        convertView.getResources().getDimensionPixelSize(R.dimen.monthly_goals_category_listView_lastItem_paddingBottom) : 0
        );
        convertView.setLongClickable(true);

        return new CategoryPlansViewHolder(convertView, null);
    }

    @Override
    public void onItemDragMoved(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void updateSourceCollection(Collection<CategoryPlansViewModel> newSourceCollection) {
        this.getSourceList().updateSourceList(newSourceCollection);
        super.updateSourceCollection(newSourceCollection);
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
                    getView().getContext(),
                    getLayoutInflater(),
                    dateTimeChangeListenersRegistry,
                    yearMonthPair,
                    new ArrayList<DailyPlansViewModel>());

            this.daysListListRecyclerView.setAdapter(adapter);
            adapter.updateSourceCollection(modelObject.getDailyPlansList());
        }
    }
}
