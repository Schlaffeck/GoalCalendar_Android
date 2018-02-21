package com.slamcode.goalcalendar.commands;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 14.02.2017.
 */

public abstract class CommandBaseImpl<Parameter, Result> implements Command<Parameter, Result> {

    private CommandStatus executionStatus = CommandStatus.NotStarted;

    private Exception executionException;

    private Exception revertingException;

    private List<CommandStateChangedListener> commandStateChangedListeners = new ArrayList<>();

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
            this.setCommandStatus(CommandStatus.Executing);
            try
            {
                result = this.executeCore(parameter);
                this.setCommandStatus(CommandStatus.Executed);
            }
            catch(Exception ex)
            {
                this.executionException = ex;
                this.setCommandStatus(CommandStatus.ExecutionFailed);
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
            this.setCommandStatus(CommandStatus.Reverting);
            try
            {
                this.revertCore();
                this.setCommandStatus(CommandStatus.Reverted);
            }
            catch(Exception ex)
            {
                this.revertingException = ex;
                this.setCommandStatus(CommandStatus.RevertingFailed);
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void addCommandStateChangedListener(CommandStateChangedListener listener)
    {
        if(listener == null)
                return;

        if(this.commandStateChangedListeners.contains(listener))
            return;;

            this.commandStateChangedListeners.add(listener);
    }

    @Override
    public void removeCommandStateChangedListener(CommandStateChangedListener listener)
    {
        if(listener == null)
            return;

        this.commandStateChangedListeners.remove(listener);
    }

    @Override
    public void clearCommandStateChangedListeners() {
        this.commandStateChangedListeners.clear();
    }

    protected void onCommandStatusChanged(CommandStatus oldStatus, CommandStatus newStatus)
    {
        for(CommandStateChangedListener listener : this.commandStateChangedListeners)
            listener.onStateChanged(oldStatus, newStatus);
    }

    protected abstract Result executeCore(Parameter parameter);

    protected abstract void revertCore();

    private void setCommandStatus(CommandStatus status)
    {
        CommandStatus old = this.executionStatus;
        this.executionStatus = status;
        this.onCommandStatusChanged(old, status);
    }
}
