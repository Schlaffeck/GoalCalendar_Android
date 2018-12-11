package com.slamcode.goalcalendar.view.dialogs;

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
import com.slamcode.goalcalendar.data.model.plans.CategoryModel;
import com.slamcode.goalcalendar.data.model.plans.DailyPlanModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.dialogs.base.ModelBasedDialog;
import com.slamcode.goalcalendar.view.utils.ViewReference;
import com.slamcode.goalcalendar.view.utils.ViewOnClick;
import static com.slamcode.goalcalendar.view.utils.ViewBinder.*;
import com.slamcode.goalcalendar.view.validation.CategoryNameTextValidator;
import com.slamcode.goalcalendar.view.utils.ResourcesHelper;
import com.slamcode.goalcalendar.view.utils.SpinnerHelper;
import com.slamcode.goalcalendar.view.validation.base.TextViewValidator;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;
import com.slamcode.goalcalendar.viewmodels.MonthViewModel;

import java.util.List;

/**
 * Created by moriasla on 06.03.2017.
 */

public class AddEditCategoryViewModelDialog extends ModelBasedDialog<CategoryPlansViewModel> {

    // views
    @ViewReference(R.id.monthly_goals_category_dialog_header_textview)
    TextView headerTextView;

    @ViewReference(R.id.monthly_goals_category_dialog_name_edittext)
    EditText categoryNameEditText;

    @ViewReference(R.id.monthly_goals_category_dialog_period_spinner)
    Spinner frequencyPeriodSpinner;

    @ViewReference(R.id.monthly_goals_category_dialog_frequency_numberpicker)
    NumberPicker frequencyValueNumberPicker;

    @ViewReference(R.id.monthly_goals_category_dialog_add_button)
    Button addButton;

    @ViewReference(R.id.monthly_goals_category_dialog_cancel_button)
    Button cancelButton;

    MonthViewModel monthViewModel;

    private PlansSummaryCalculator plansSummaryCalculator;

    public void setMonthViewModel(MonthViewModel newMonthViewModel)
    {
        this.monthViewModel = newMonthViewModel;
    }

    public void setPlansSummaryCalculator(PlansSummaryCalculator plansSummaryCalculator) {
        this.plansSummaryCalculator = plansSummaryCalculator;
    }

    @Override
    protected View initializeView(LayoutInflater inflater)
    {
        View view = inflater.inflate(R.layout.monthly_goals_add_edit_category_dialog_layout, null);
        this.headerTextView = findView(view, R.id.monthly_goals_category_dialog_header_textview);
        this.categoryNameEditText = findView(view, R.id.monthly_goals_category_dialog_name_edittext);
        this.frequencyPeriodSpinner = findView(view, R.id.monthly_goals_category_dialog_period_spinner);
        this.frequencyValueNumberPicker = findView(view, R.id.monthly_goals_category_dialog_frequency_numberpicker);
        this.addButton = findView(view, R.id.monthly_goals_category_dialog_add_button);
        this.cancelButton = findView(view, R.id.monthly_goals_category_dialog_cancel_button);

        // setFrequencyPeriod view data
        this.headerTextView.setText(
                this.getModel() == null ?
                        R.string.add_category_dialog_header
                        : R.string.edit_category_dialog_header);


        final TextViewValidator nameValidator =new CategoryNameTextValidator(this.categoryNameEditText);
        this.getViewValidatorList().add(nameValidator);
        this.categoryNameEditText.addTextChangedListener(nameValidator);

        this.frequencyValueNumberPicker.setMaxValue(31);
        this.frequencyValueNumberPicker.setValue(1);
        this.frequencyValueNumberPicker.setMinValue(1);

        ArrayAdapter<String> periodStringsAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                ResourcesHelper.frequencyPeriodResourceStrings(this.getActivity()));
        this.frequencyPeriodSpinner.setAdapter(periodStringsAdapter);

        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitChanges();
            }
        });

        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });

        // setFrequencyPeriod values
        if(this.getModel() != null)
        {
            this.categoryNameEditText.setText(this.getModel().getName());

            SpinnerHelper.setSelectedValue(this.frequencyPeriodSpinner, ResourcesHelper.toResourceString(
                    this.getActivity(),
                    this.getModel().getFrequencyPeriod()));
            this.frequencyValueNumberPicker.setValue(this.getModel().getFrequencyValue());
        }

        return view;
    }

    @Override
    @ViewOnClick(R.id.monthly_goals_category_dialog_cancel_button)
    protected void cancelChanges()
    {
        super.cancelChanges();
    }

    @Override
    @ViewOnClick(R.id.monthly_goals_category_dialog_add_button)
    protected void commitChanges()
    {
        if(!this.isAllValid())
        {
            return;
        }

        if(this.getModel() == null)
        {
            CategoryModel newCategory = new CategoryModel();

            newCategory.setDailyPlans(CollectionUtils.createList(
                    DateTimeHelper.getDaysCount(this.monthViewModel.getYear(), this.monthViewModel.getMonth()),
                    new ElementCreator<DailyPlanModel>() {
                        @Override
                        public DailyPlanModel Create(int index, List<DailyPlanModel> currentList) {
                            return new DailyPlanModel(index+1, PlanStatus.Empty, index+1);
                        }
                    }));
            this.setModel(new CategoryPlansViewModel(this.monthViewModel, newCategory, this.plansSummaryCalculator));
        }

        CategoryPlansViewModel m = this.getModel();
        m.setName(this.categoryNameEditText.getText().toString());

        m.setFrequencyValue(this.frequencyValueNumberPicker.getValue());

        Object periodObject = this.frequencyPeriodSpinner.getSelectedItem();
        m.setFrequencyPeriod(ResourcesHelper.frequencyPeriodFromResourceString(
                this.getActivity(),
                periodObject.toString()));
       super.commitChanges();
    }
}
