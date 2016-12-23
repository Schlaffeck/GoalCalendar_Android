package com.slamcode.goalcalendar.view.dialogs;

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
import com.slamcode.goalcalendar.view.AddEditCategoryDialog;
import com.slamcode.goalcalendar.view.CategoryNameTextValidator;
import com.slamcode.goalcalendar.view.ResourcesHelper;
import com.slamcode.goalcalendar.view.validation.TextViewValidator;
import com.slamcode.goalcalendar.view.validation.ViewValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 23.12.2016.
 */

public abstract class AddEditDialog<ModelType> extends DialogFragment {

    private View associatedView;

    private ModelType model;

    private boolean confirmed;

    private List<ViewValidator<?>> viewValidatorList =  new ArrayList<>();

    private AddEditCategoryDialog.DialogStateChangedListener dialogStateChangedListener;

    public View getAssociatedView()
    {
        return this.associatedView;
    }

    public ModelType getModel() {
        return model;
    }

    public void setModel(ModelType model) {
        this.model = model;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    protected void setConfirmed(boolean newValue)
    {
        this.confirmed = newValue;
    }

    protected List<ViewValidator<?>> getViewValidatorList() {
        return viewValidatorList;
    }

    public void setDialogStateChangedListener(AddEditCategoryDialog.DialogStateChangedListener dialogStateChangedListener) {
        this.dialogStateChangedListener = dialogStateChangedListener;
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

    protected abstract View initializeView(LayoutInflater inflater);

    protected void cancelChanges()
    {
        this.confirmed = false;
        this.getDialog().cancel();
        this.onDialogClosed();
    }

    protected abstract void commitChanges();

    protected boolean isAllValid()
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
