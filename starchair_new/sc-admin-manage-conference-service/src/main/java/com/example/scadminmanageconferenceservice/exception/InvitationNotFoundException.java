package com.example.scadminmanageconferenceservice.exception;

public class InvitationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public InvitationNotFoundException() {
        super("Invitation not found!");
    }
}
