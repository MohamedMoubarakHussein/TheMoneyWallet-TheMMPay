package com.themoneywallet.sharedUtilities.patterns.command;

/**
 * Command interface following Command Pattern
 * Encapsulates a request as an object, allowing for parameterization and queuing of requests
 */
public interface Command<T, R> {
    
    /**
     * Executes the command with the provided input
     * @param input the input data for command execution
     * @return the result of command execution
     */
    R execute(T input);
    
    /**
     * Validates if the command can be executed with the given input
     * @param input the input data to validate
     * @return true if command can be executed, false otherwise
     */
    default boolean canExecute(T input) {
        return input != null;
    }
    
    /**
     * Returns the command name for logging and debugging
     * @return command name
     */
    String getCommandName();
}

