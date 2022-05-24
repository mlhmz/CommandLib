package de.trace.lib.command;

public class SuccessfulCommand extends RuntimeException {
    public SuccessfulCommand(String message) {
        super(message);
    }
}
