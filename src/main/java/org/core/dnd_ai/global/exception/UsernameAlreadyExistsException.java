package org.core.dnd_ai.global.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() {
        super("Username is already exists");
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
