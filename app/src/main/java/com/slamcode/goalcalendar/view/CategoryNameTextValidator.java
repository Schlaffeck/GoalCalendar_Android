package com.slamcode.goalcalendar.view;

import android.widget.TextView;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.validation.TextViewValidator;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by moriasla on 22.12.2016.
 */

public class CategoryNameTextValidator extends TextViewValidator {

    public CategoryNameTextValidator(TextView textView)
    {
        super(textView);
    }

    @Override
    protected TextValidationResult validateInternal(TextView textView) {
        TextValidationResult result = new TextValidationResult();
        if(StringUtils.isEmpty(textView.getText().toString()))
        {
            result.setValid(false);
            result.setValidationErrorMessageResourceId(R.string.monthly_goals_category_dialog_Validation_name_edittext_error_empty);
        }
        else
        {
            result.setValid(true);
        }
        return result;
    }
}
