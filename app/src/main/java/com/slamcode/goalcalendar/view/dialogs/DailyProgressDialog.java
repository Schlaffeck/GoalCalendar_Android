package com.slamcode.goalcalendar.view.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryDescriptionProvider;
import com.slamcode.goalcalendar.view.dialogs.base.ModelBasedDialog;
import com.slamcode.goalcalendar.view.utils.ViewBinder;
import com.slamcode.goalcalendar.view.utils.ViewReference;

/**
 * Dialog with daily progress notification
 */

public final class DailyProgressDialog extends ModelBasedDialog<PlansSummaryDescriptionProvider.PlansSummaryDescription> {

    @ViewReference(R.id.daily_progress_dialog_title_textView)
    TextView titleTextView;

    @ViewReference(R.id.daily_progress_dialog_description_textView)
    TextView descriptionTextView;

    @Override
    protected View initializeView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.daily_plans_progress_dialog, null);

        ViewBinder.bindViews(this, view);

        this.titleTextView.setText(this.getModel().getTitle());
        this.descriptionTextView.setText(this.getModel().getDetails());

        return view;
    }
}