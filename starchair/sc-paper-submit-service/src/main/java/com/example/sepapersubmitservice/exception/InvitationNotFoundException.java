package com.example.sepapersubmitservice.exception;

public class InvitationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public InvitationNotFoundException() {
        super("Invitation not found!");
    }
}
