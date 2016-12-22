package com.slamcode.goalcalendar.view;

import android.text.Editable;
import android.view.View;
import android.widget.TextView;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.validation.TextValidator;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by moriasla on 22.12.2016.
 */

public class CategoryNameTextValidator extends TextValidator {

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
