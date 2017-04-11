package com.slamcode.goalcalendar.view.validation.base;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

/**
 * Created by moriasla on 22.12.2016.
 */

public abstract class TextViewValidator implements ViewValidator<TextView>, View.OnFocusChangeListener, TextWatcher {

    private final TextView textView;

    protected TextViewValidator(TextView textView) {
        this.textView = textView;
    }

    @Override
    public boolean isValid()
    {
        return this.validate(this.textView);
    }

    @Override
    public boolean validate(TextView textView)
    {
        TextValidationResult result = validateInternal(textView);
        if(result.isValid())
        {
            textView.setError(null);
            return true;
        }

        if(result.getValidationErrorMessageResourceId() > 0)
        {
            textView.setError(
                    textView.getResources()
                    .getString(result.getValidationErrorMessageResourceId()));
        }
        else {
            textView.setError(result.getValidationErrorMessage());
        }
        return false;
    }

    protected abstract TextValidationResult validateInternal(TextView textView);

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(!hasFocus)
        {
            validate((TextView)view);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        validate(this.textView);
    }

    public class TextValidationResult
    {
        private boolean valid;

        private int validationErrorMessageResourceId;

        private String validationErrorMessage;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getValidationErrorMessage() {
            return validationErrorMessage;
        }

        public void setValidationErrorMessage(String validationErrorMessage) {
            this.validationErrorMessage = validationErrorMessage;
        }

        public int getValidationErrorMessageResourceId() {
            return validationErrorMessageResourceId;
        }

        public void setValidationErrorMessageResourceId(int validationErrorMessageResourceId) {
            this.validationErrorMessageResourceId = validationErrorMessageResourceId;
        }
    }
}
