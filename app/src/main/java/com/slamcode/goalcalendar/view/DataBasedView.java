package com.slamcode.goalcalendar.view;

import android.databinding.Bindable;

/**
 * Created by moriasla on 06.03.2017.
 */

public interface DataBasedView<DataViewModel> {

    void onDataSet(DataViewModel data);
}
