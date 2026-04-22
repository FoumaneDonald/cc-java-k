package com.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String from, String to) {
        super("Cannot transition leave request from " + from + " to " + to);
    }
}
