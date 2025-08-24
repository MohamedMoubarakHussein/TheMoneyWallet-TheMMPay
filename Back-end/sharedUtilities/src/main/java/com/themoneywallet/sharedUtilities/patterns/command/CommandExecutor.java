package com.themoneywallet.sharedUtilities.patterns.command;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Command executor following Command Pattern
 * Provides centralized execution of commands with logging and error handling
 */
@Component
@Slf4j
public class CommandExecutor {
    
    /**
     * Executes a command with proper logging and error handling
     * @param command the command to execute
     * @param input the input for the command
     * @param <T> input type
     * @param <R> result type
     * @return the result of command execution
     */
    public <T, R> R execute(Command<T, R> command, T input) {
        String commandName = command.getCommandName();
        log.info("Executing command: {}", commandName);
        
        try {
            if (!command.canExecute(input)) {
                log.error("Command {} cannot be executed with provided input", commandName);
                throw new IllegalArgumentException("Invalid input for command: " + commandName);
            }
            
            R result = command.execute(input);
            log.info("Command {} executed successfully", commandName);
            return result;
            
        } catch (Exception e) {
            log.error("Error executing command {}: {}", commandName, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Executes a command that returns ResponseEntity with additional error handling
     */
    public <T> ResponseEntity<UnifiedResponse> executeWithResponse(Command<T, ResponseEntity<UnifiedResponse>> command, T input) {
        try {
            return execute(command, input);
        } catch (Exception e) {
            log.error("Command execution failed: {}", e.getMessage(), e);
            // Return a generic error response - this should be customized based on requirements
            return ResponseEntity.internalServerError().build();
        }
    }
}

