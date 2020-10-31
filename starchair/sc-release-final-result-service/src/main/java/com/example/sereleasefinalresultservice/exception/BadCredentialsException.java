package com.example.sereleasefinalresultservice.exception;

public class BadCredentialsException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public BadCredentialsException() {
        super("Password incorrect!");
    }
}
