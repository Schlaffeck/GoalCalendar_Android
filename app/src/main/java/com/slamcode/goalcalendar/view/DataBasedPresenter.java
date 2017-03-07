package com.slamcode.goalcalendar.view;

/**
 * Created by moriasla on 06.03.2017.
 */

public interface DataBasedPresenter<DataViewModel> {

    DataViewModel getData();

    void setData(DataViewModel data);
}
