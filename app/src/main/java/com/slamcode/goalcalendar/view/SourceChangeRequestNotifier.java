package com.slamcode.goalcalendar.view;

import com.slamcode.goalcalendar.view.lists.base.RecyclerViewDataAdapter;

/**
 * Created by moriasla on 09.03.2017.
 */

public interface SourceChangeRequestNotifier<SourceType> {

    void addSourceChangeRequestListener(SourceChangeRequestListener<SourceType> listener);

    void removeSourceChangeRequestListener(SourceChangeRequestListener<SourceType> listener);

    void clearSourceChangeRequestListeners();

    void notifySourceChangeRequested(SourceChangeRequest request);

    interface SourceChangeRequestListener<SourceType>
    {
        void sourceChangeRequested(SourceType sender, SourceChangeRequest request);
    }

    abstract class SourceChangeRequest
    {
        public abstract int getId();
    }
}
