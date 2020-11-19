package com.example.sepapersubmitservice.exception;

public class ScoreNotConfirmedException extends RuntimeException {
    public ScoreNotConfirmedException() {
        super("Some Pc haven't confirmed the scores!");
    }
}
