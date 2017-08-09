package com.slamcode.goalcalendar.view.lists;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangeListener;
import com.slamcode.goalcalendar.planning.schedule.DateTimeChangeListenersRegistry;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.planning.YearMonthPair;
import com.slamcode.goalcalendar.view.BaseSourceChangeRequest;
import com.slamcode.goalcalendar.view.controls.GoalPlanStatusButton;
import com.slamcode.goalcalendar.view.lists.base.ComparatorSortedListCallback;
import com.slamcode.goalcalendar.view.lists.base.DefaultComparator;
import com.slamcode.goalcalendar.view.lists.base.RecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.view.utils.ColorsHelper;
import com.slamcode.goalcalendar.viewmodels.DailyPlansViewModel;

import java.util.Collection;
import java.util.Date;


/**
 * Created by moriasla on 06.02.2017.
 */

public class DailyPlanRecyclerViewAdapter extends RecyclerViewDataAdapter<DailyPlansViewModel, DailyPlanRecyclerViewAdapter.DailyPlanViewHolder>{

    private final YearMonthPair yearMonthPair;
    private final DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry;

    protected DailyPlanRecyclerViewAdapter(Context context,
                                           LayoutInflater layoutInflater,
                                           DateTimeChangeListenersRegistry dateTimeChangeListenersRegistry,
                                           YearMonthPair yearMonthPair,
                                           Collection<DailyPlansViewModel> sourceCollection) {
        super(context, layoutInflater,
                new SortedList<>(
                        DailyPlansViewModel.class,
                        new ComparatorSortedListCallback<>(new DefaultComparator<DailyPlansViewModel>())));
        this.yearMonthPair = yearMonthPair;
        this.dateTimeChangeListenersRegistry = dateTimeChangeListenersRegistry;
        this.updateSourceCollection(sourceCollection);
    }

    @Override
    public DailyPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = this.getLayoutInflater().inflate(R.layout.monthly_goals_plan_status_cell, null);
        return new DailyPlanViewHolder(view);
    }

    private boolean isCurrentDate(DailyPlansViewModel dailyPlanModel)
    {
        if(this.yearMonthPair == null)
            return false;

        int dayNumber = dailyPlanModel.getDayNumber();
        return DateTimeHelper.isTodayDate(
                this.yearMonthPair.getYear(),
                this.yearMonthPair.getMonth(),
                dayNumber);
    }

    private boolean isPassedDate(DailyPlansViewModel dailyPlansViewModel)
    {
        if(this.yearMonthPair == null)
        return false;
        boolean isPassed = DateTimeHelper.isDateBefore(new DateTime(this.yearMonthPair.getYear(), this.yearMonthPair.getMonth(), dailyPlansViewModel.getDayNumber()), DateTimeHelper.getTodayDateTime());

        return isPassed;
    }

    /**
     * View holder for daily plan
     */
    public class DailyPlanViewHolder extends BindableViewHolderBase<DailyPlansViewModel> implements DateTimeChangeListener, View.OnLongClickListener{

        @ViewReference(R.id.plan_status_list_item_view_button)
        GoalPlanStatusButton statusButton;

        public DailyPlanViewHolder(View view) {

            super(view);
        }

        @Override
        public void bindToModel(final DailyPlansViewModel modelObject) {
            super.bindToModel(modelObject);

            if(isPassedDate(modelObject))
                this.statusButton.setOmittedStatuses(PlanStatus.Planned);

            this.statusButton.addOnStateChangedListener(new GoalPlanStatusButton.OnStateChangedListener() {
                @Override
                public void onStateChanged(PlanStatus newState) {
                    modelObject.setStatus(newState);
                }
            });

            if(isCurrentDate(modelObject))
                ColorsHelper.setSecondAccentBackgroundColor(this.getView());

            dateTimeChangeListenersRegistry.registerListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            this.getModelObject().notifySourceChangeRequested(new BaseSourceChangeRequest(DailyPlansViewModel.REQUEST_EDIT_DAILY_PLANS));
            return false;
        }

        @Override
        public void onDateChanged() {

            if(isCurrentDate(this.getModelObject()))
                ColorsHelper.setSecondAccentBackgroundColor(this.getView());
            else
                ColorsHelper.setListItemBackgroundColor(this.getView());
        }
    }
}
