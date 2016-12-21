package com.slamcode.goalcalendar.view;

import android.app.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.FrequencyModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;

import java.util.List;

/**
 * Created by moriasla on 21.12.2016.
 */

public class AddEditCategoryDialog extends DialogFragment {

    private View associatedView;
    private CategoryModel model;

    private boolean confirmed;

    public CategoryModel getModel() {
        return model;
    }

    public void setModel(CategoryModel model) {
        this.model = model;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        LayoutInflater inflater = this.getActivity().getLayoutInflater();

        if(this.associatedView == null)
        {
            this.associatedView = this.initializeView(inflater);
        }

        builder.setView(this.associatedView);
        return builder.create();
    }

    private View initializeView(LayoutInflater inflater)
    {
        View view = inflater.inflate(R.layout.monthly_goals_add_edit_category_dialog_layout, null);

        // set view data
        TextView headerTextView = (TextView) view.findViewById(R.id.monthly_goals_category_dialog_header_textview);
        headerTextView.setText(
                this.getModel() == null ?
                        R.string.add_category_dialog_header
                        : R.string.edit_category_dialog_header);

        NumberPicker picker = (NumberPicker) view.findViewById(R.id.monthly_goals_category_dialog_frequency_numberpicker);
        picker.setMaxValue(31);
        picker.setValue(0);
        picker.setMinValue(0);

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

        return view;
    }

    private void cancelChanges()
    {
        this.confirmed = false;
        this.getDialog().cancel();
    }

    private void commitChanges()
    {
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
                ((EditText)this.associatedView.findViewById(R.id.monthly_goals_category_dialog_name_edittext))
                        .getText().toString());

        m.getFrequency().setFrequencyValue(
                ((NumberPicker)this.associatedView.findViewById(R.id.monthly_goals_category_dialog_frequency_numberpicker))
                        .getValue());

        Object periodObject = ((Spinner)this.associatedView.findViewById(R.id.monthly_goals_category_dialog_period_spinner))
                .getSelectedItem();
        m.getFrequency()
                .setPeriod(ResourcesHelper.frequencyPeriodFromResourceString(
                    this.getActivity(),
                    periodObject.toString()));
        this.confirmed = true;
    }
}
