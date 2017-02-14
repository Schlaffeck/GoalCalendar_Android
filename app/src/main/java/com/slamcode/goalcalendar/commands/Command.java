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
}
