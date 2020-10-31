package com.example.scgetinvitionalsentservice.exception;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public UserNotFoundException(){
        super("User not found!");
    }
}
