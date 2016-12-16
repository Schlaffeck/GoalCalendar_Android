package com.slamcode.goalcalendar.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.PlanStatus;
import com.slamcode.goalcalendar.view.base.ListViewDataAdapter;
import com.slamcode.goalcalendar.view.base.ViewHolderBase;

/**
 * Created by moriasla on 16.12.2016.
 */

public class PlanStatusListViewAdapter extends ListViewDataAdapter<DailyPlanModel, ViewHolderBase<DailyPlanModel>> {

    private final CategoryModel categoryModel;

    public PlanStatusListViewAdapter(Context context, LayoutInflater layoutInflater, CategoryModel categoryModel)
    {
        super(context, layoutInflater);
        this.categoryModel = categoryModel;
    }

    @Override
    protected ViewHolderBase getNewViewHolder(View convertView) {
        if(convertView == null)
        {
            convertView = this.getLayoutInflater().inflate(R.layout.plan_status_list_item_view, null);
        }

        ViewHolderBase<PlanStatus> viewHolder = new ViewHolderBase<PlanStatus>(convertView);

        return viewHolder;
    }

    @Override
    protected void fillListElementView(final DailyPlanModel planStatus, final ViewHolderBase<DailyPlanModel> viewHolder) {

        viewHolder.setBaseObject(planStatus);

        Button button = (Button)viewHolder.getView().findViewById(R.id.plan_status_list_item_view_button);
        button.setText(planStatus.getStatus().toString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button  btn = (Button) view;
                planStatus.setStatus(planStatus.getStatus().nextStatus());
                btn.setText(planStatus.getStatus().toString());
            }
        });
    }
}
