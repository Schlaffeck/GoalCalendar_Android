package com.slamcode.goalcalendar.view.lists.base.selection;

import java.util.ArrayList;

public interface MultiSelector {

    ArrayList<Integer> getSelectedItemIds();

    void toggleSelection(SelectableViewHolder selectableViewHolder);
}
