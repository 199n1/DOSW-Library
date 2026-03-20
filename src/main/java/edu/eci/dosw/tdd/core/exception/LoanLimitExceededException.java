package edu.eci.dosw.tdd.core.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}