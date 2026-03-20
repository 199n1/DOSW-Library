package edu.eci.dosw.tdd.core.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}