package com.slamcode.goalcalendar.view.lists.base.selection;

import java.util.ArrayList;

public interface MultiSelector {

    ArrayList<Integer> getSelectedItemIds();

    boolean toggleSelection(SelectableViewHolder selectableViewHolder);
}
