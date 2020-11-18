package com.example.scadminmanageconferenceservice.exception;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public UserNotFoundException(){
        super("User not found!");
    }
}
