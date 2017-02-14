package com.slamcode.goalcalendar.commands;

import java.security.Policy;

/**
 * Created by moriasla on 14.02.2017.
 */

public abstract class CommandBaseImpl<Parameter, Result> implements Command<Parameter, Result> {

    private CommandStatus executionStatus = CommandStatus.NotStarted;

    private Exception executionException;

    private Exception revertingException;

    @Override
    public CommandStatus getExecutionStatus() {
        return this.executionStatus;
    }

    @Override
    public Exception getExecutionException() {
        return this.executionException;
    }

    @Override
    public Exception getRevertingException() {
        return this.revertingException;
    }

    @Override
    public boolean canExecute(Parameter parameter) {
        return this.executionStatus == CommandStatus.NotStarted;
    }

    @Override
    public Result execute(Parameter parameter) {
        Result result = null;
        if(canExecute(parameter))
        {
            this.executionStatus = CommandStatus.Executing;
            try
            {
                result = this.executeCore(parameter);
                this.executionStatus = CommandStatus.Executed;
            }
            catch(Exception ex)
            {
                this.executionException = ex;
                this.executionStatus = CommandStatus.ExecutionFailed;
                ex.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public boolean canRevert() {
        return this.executionStatus == CommandStatus.ExecutionFailed
                || this.executionStatus == CommandStatus.Executed;
    }

    @Override
    public void revert() {
        if(canRevert())
        {
            this.executionStatus = CommandStatus.Reverting;
            try
            {
                this.revertCore();
                this.executionStatus = CommandStatus.Reverted;
            }
            catch(Exception ex)
            {
                this.revertingException = ex;
                this.executionStatus = CommandStatus.RevertingFailed;
                ex.printStackTrace();
            }
        }
    }

    protected abstract Result executeCore(Parameter parameter);

    protected abstract void revertCore();
}
