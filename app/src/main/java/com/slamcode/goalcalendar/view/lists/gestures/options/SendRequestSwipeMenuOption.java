package com.slamcode.goalcalendar.view.lists.gestures.options;

import com.slamcode.goalcalendar.view.BaseSourceChangeRequest;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;
import com.slamcode.goalcalendar.view.lists.gestures.SwipeMenuOption;

/**
 * This menu option simply sends id of request when selected
 */

public final class SendRequestSwipeMenuOption extends SwipeMenuOption {

    private int actionRequestId;
    private final SourceChangeRequestNotifier requestNotifier;

    public SendRequestSwipeMenuOption(SwipeMenuOption.ViewInflater viewInflater, int actionRequestId, SourceChangeRequestNotifier requestNotifier) {
        super(viewInflater);
        this.actionRequestId = actionRequestId;
        this.requestNotifier = requestNotifier;
    }

    @Override
    public void optionSelectedAction() {
        requestNotifier.notifySourceChangeRequested(new BaseSourceChangeRequest(this.getActionRequestId()));
    }

    public int getActionRequestId() {
        return actionRequestId;
    }
}
