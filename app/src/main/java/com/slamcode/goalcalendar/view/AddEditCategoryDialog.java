package com.slamcode.goalcalendar.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.FrequencyModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.view.dialogs.AddEditDialog;
import com.slamcode.goalcalendar.view.validation.TextViewValidator;
import com.slamcode.goalcalendar.view.validation.ViewValidator;

import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 21.12.2016.
 */
public class AddEditCategoryDialog extends AddEditDialog<CategoryModel> {

    @Override
    protected View initializeView(LayoutInflater inflater)
    {
        View view = inflater.inflate(R.layout.monthly_goals_add_edit_category_dialog_layout, null);

        // set view data
        TextView headerTextView = (TextView) view.findViewById(R.id.monthly_goals_category_dialog_header_textview);
        headerTextView.setText(
                this.getModel() == null ?
                        R.string.add_category_dialog_header
                        : R.string.edit_category_dialog_header);

        final EditText nameEditTextView
                = (EditText)view.findViewById(R.id.monthly_goals_category_dialog_name_edittext);

        final TextViewValidator nameValidator =new CategoryNameTextValidator(nameEditTextView);
        this.getViewValidatorList().add(nameValidator);
        nameEditTextView.addTextChangedListener(nameValidator);

        NumberPicker frequencyValuePicker = (NumberPicker) view.findViewById(R.id.monthly_goals_category_dialog_frequency_numberpicker);
        frequencyValuePicker.setMaxValue(100);
        frequencyValuePicker.setValue(0);
        frequencyValuePicker.setMinValue(0);

        Spinner periodSpinner = (Spinner) view.findViewById(R.id.monthly_goals_category_dialog_period_spinner);
        ArrayAdapter<String> periodStringsAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_spinner_item,
                ResourcesHelper.frequencyPeriodResourceStrings(this.getActivity()));
        periodSpinner.setAdapter(periodStringsAdapter);

        // assign buttons actions
        final Button cancelButton = (Button) view.findViewById(R.id.monthly_goals_category_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelChanges();
            }
        });

        final Button confirmButton = (Button) view.findViewById(R.id.monthly_goals_category_dialog_add_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    commitChanges();
                    getDialog().dismiss();
            }
        });

        // set values
        if(this.getModel() != null)
        {
            nameEditTextView.setText(this.getModel().getName());

            int position = periodStringsAdapter.getPosition(
                    ResourcesHelper.toResourceString(
                            this.getActivity(),
                            this.getModel().getFrequency().getPeriod()));
            periodSpinner.setSelection(position);

            frequencyValuePicker.setValue(this.getModel().getFrequency().getFrequencyValue());
        }

        return view;
    }

    @Override
    protected void commitChanges()
    {
        if(!this.isAllValid())
        {
            return;
        }

        if(this.getModel() == null)
        {
            CategoryModel newModel = new CategoryModel();

            // todo: provide month and number of days in it
            newModel.setDailyPlans(CollectionUtils.createList(31, new ElementCreator<DailyPlanModel>() {
                @Override
                public DailyPlanModel Create(int index, List<DailyPlanModel> currentList) {
                    return new DailyPlanModel();
                }
            }));

            FrequencyModel frequencyModel = new FrequencyModel();
            newModel.setFrequency(frequencyModel);
            this.setModel(newModel);
        }

        CategoryModel m = this.getModel();
        m.setName(
                ((EditText)this.getAssociatedView().findViewById(R.id.monthly_goals_category_dialog_name_edittext))
                        .getText().toString());

        m.getFrequency().setFrequencyValue(
                ((NumberPicker)this.getAssociatedView().findViewById(R.id.monthly_goals_category_dialog_frequency_numberpicker))
                        .getValue());

        Object periodObject = ((Spinner)this.getAssociatedView().findViewById(R.id.monthly_goals_category_dialog_period_spinner))
                .getSelectedItem();
        m.getFrequency()
                .setPeriod(ResourcesHelper.frequencyPeriodFromResourceString(
                    this.getActivity(),
                    periodObject.toString()));
        this.setConfirmed(true);
        this.onDialogClosed();
    }
}
