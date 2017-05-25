package com.slamcode.goalcalendar.view.dialogs;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.dialogs.base.AddEditDialog;
import com.slamcode.goalcalendar.view.utils.ViewBinder;
import com.slamcode.goalcalendar.view.utils.ViewOnClick;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.viewmodels.DailyPlansViewModel;

import java.util.Date;

/**
 * Created by smoriak on 25/05/2017.
 */

public class EditDailyPlansViewModelDialog extends AddEditDialog<EditDailyPlansViewModelDialog.DailyPlansDialogData> {

    @ViewReference(R.id.edit_dailyPlans_dialog_description_edittext)
    EditText descriptionEditText;

    @ViewReference(R.id.edit_dailyPlans_dialog_header_textview)
    TextView headerTextView;

    @Override
    protected View initializeView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.monthly_goals_edit_daily_plans_dialog_layout, null);

        ViewBinder.bindViews(this, view);

        this.headerTextView.setText(
                String.format(
                        this.getString(R.string.edit_dailyPlans_dialog_description_header),
                        getModel().categoryName,
                        getModel().date));

        if(this.getModel().dailyPlansViewModel.getDescription() != null)
            this.descriptionEditText.setText(this.getModel().dailyPlansViewModel.getDescription());

        return view;
    }

    @Override
    @ViewOnClick(R.id.edit_dailyPlans_dialog_confirm_button)
    protected void commitChanges() {
        this.getModel().dailyPlansViewModel.setDescription(this.descriptionEditText.getText().toString());
        this.setConfirmed(true);
        this.getDialog().dismiss();
        this.onDialogClosed();
    }

    @Override
    @ViewOnClick(R.id.edit_dailyPlans_dialog_cancel_button)
    protected void cancelChanges()
    {
        super.cancelChanges();
    }

    public class DailyPlansDialogData
    {
        private final String categoryName;
        private final DailyPlansViewModel dailyPlansViewModel;
        private final Date date;

        public DailyPlansDialogData(String categoryName, Date date, DailyPlansViewModel dailyPlansViewModel)
        {
            this.categoryName = categoryName;
            this.dailyPlansViewModel = dailyPlansViewModel;
            this.date = date;
        }
    }
}
