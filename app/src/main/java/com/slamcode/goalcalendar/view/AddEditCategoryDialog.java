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
import com.slamcode.goalcalendar.view.utils.SpinnerHelper;
import com.slamcode.goalcalendar.view.validation.TextViewValidator;
import com.slamcode.goalcalendar.view.validation.ViewValidator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by moriasla on 21.12.2016.
 */
public class AddEditCategoryDialog extends DialogFragment {

    // views
    @BindView(R.id.monthly_goals_category_dialog_header_textview)
    TextView headerTextView;

    @BindView(R.id.monthly_goals_category_dialog_name_edittext)
    EditText categoryNameEditText;

    @BindView(R.id.monthly_goals_category_dialog_period_spinner)
    Spinner frequencyPeriodSpinner;

    @BindView(R.id.monthly_goals_category_dialog_frequency_numberpicker)
    NumberPicker frequencyValueNumberPicker;

    // dependencies
    private CategoryModel model;

    private boolean confirmed;

    private List<ViewValidator<?>> viewValidatorList =  new ArrayList<>();

    private DialogStateChangedListener dialogStateChangedListener;

    public CategoryModel getModel() {
        return model;
    }

    public void setModel(CategoryModel model) {
        this.model = model;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    protected List<ViewValidator<?>> getViewValidatorList() {
        return viewValidatorList;
    }

    public void setDialogStateChangedListener(DialogStateChangedListener dialogStateChangedListener) {
        this.dialogStateChangedListener = dialogStateChangedListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        LayoutInflater inflater = this.getActivity().getLayoutInflater();
        View view = this.initializeView(inflater);

        builder.setView(view);
        return builder.create();
    }

    private View initializeView(LayoutInflater inflater)
    {
        View view = inflater.inflate(R.layout.monthly_goals_add_edit_category_dialog_layout, null);

        ButterKnife.bind(this, view);

        // set view data
        this.headerTextView.setText(
                this.getModel() == null ?
                        R.string.add_category_dialog_header
                        : R.string.edit_category_dialog_header);


        final TextViewValidator nameValidator =new CategoryNameTextValidator(this.categoryNameEditText);
        this.getViewValidatorList().add(nameValidator);
        this.categoryNameEditText.addTextChangedListener(nameValidator);

        this.frequencyValueNumberPicker.setMaxValue(31);
        this.frequencyValueNumberPicker.setValue(0);
        this.frequencyValueNumberPicker.setMinValue(0);

        ArrayAdapter<String> periodStringsAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_spinner_item,
                ResourcesHelper.frequencyPeriodResourceStrings(this.getActivity()));
        this.frequencyPeriodSpinner.setAdapter(periodStringsAdapter);

        // set values
        if(this.model != null)
        {
            this.categoryNameEditText.setText(model.getName());

            SpinnerHelper.setSelectedValue(this.frequencyPeriodSpinner, ResourcesHelper.toResourceString(
                    this.getActivity(),
                    model.getPeriod()));
            this.frequencyValueNumberPicker.setValue(this.model.getFrequencyValue());
        }

        return view;
    }

    @OnClick(R.id.monthly_goals_category_dialog_cancel_button)
    void cancelChanges()
    {
        this.confirmed = false;
        this.getDialog().cancel();
        this.onDialogClosed();
    }

    @OnClick(R.id.monthly_goals_category_dialog_add_button)
    void commitChanges()
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
            this.setModel(newModel);
        }

        CategoryModel m = this.getModel();
        m.setName(this.categoryNameEditText.getText().toString());

        m.setFrequencyValue(this.frequencyValueNumberPicker.getValue());

        Object periodObject = this.frequencyPeriodSpinner.getSelectedItem();
        m.setPeriod(ResourcesHelper.frequencyPeriodFromResourceString(
                    this.getActivity(),
                    periodObject.toString()));
        this.confirmed = true;
        this.getDialog().dismiss();
        this.onDialogClosed();
    }

    private boolean isAllValid()
    {
        for (ViewValidator<?> validator : this.getViewValidatorList()){
            if(!validator.isValid())
            {
                return false;
            }
        }

        return true;
    }

    protected void onDialogClosed()
    {
        if(this.dialogStateChangedListener != null)
        {
            this.dialogStateChangedListener.onDialogClosed(this.confirmed);
        }
    }

    public interface DialogStateChangedListener
    {
        void onDialogClosed(boolean confirmed);
    }
}
