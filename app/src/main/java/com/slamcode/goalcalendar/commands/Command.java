package com.slamcode.goalcalendar.commands;

/**
 * Encapsulation of action with revert possibility
 */

public interface Command<Parameter, Result> {

    CommandStatus getExecutionStatus();

    Exception getExecutionException();

    Exception getRevertingException();

    boolean canExecute(Parameter parameter);

    Result execute(Parameter parameter);

    boolean canRevert();

    void revert();

    void addCommandStateChangedListener(CommandStateChangedListener listener);

    void removeCommandStateChangedListener(CommandStateChangedListener listener);

    void clearCommandStateChangedListeners();

    interface CommandStateChangedListener
    {
        void onStateChanged(CommandStatus oldStatus, CommandStatus newStatus);
    }
}
