package com.example.sepapersubmitservice.exception;

public class MailBoxHasBeenRegisteredException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public MailBoxHasBeenRegisteredException(){
        super("Mailbox has been registered!");
    }
}
