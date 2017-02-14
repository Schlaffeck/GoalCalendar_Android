package com.slamcode.goalcalendar.commands;

/**
 * Status of execution of command
 */

public enum CommandStatus {

    NotStarted,
    Executing,
    Executed,
    ExecutionFailed,
    Reverting,
    Reverted,
    RevertingFailed
}
