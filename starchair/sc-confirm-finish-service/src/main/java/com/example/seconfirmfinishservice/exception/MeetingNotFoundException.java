package com.example.seconfirmfinishservice.exception;

public class MeetingNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public MeetingNotFoundException(){
        super("Meeting not found!");
    }
}
