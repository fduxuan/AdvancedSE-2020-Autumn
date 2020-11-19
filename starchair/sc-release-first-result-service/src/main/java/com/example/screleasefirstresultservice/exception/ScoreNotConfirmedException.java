package com.example.screleasefirstresultservice.exception;

public class ScoreNotConfirmedException extends RuntimeException {
    public ScoreNotConfirmedException() {
        super("Some Pc haven't confirmed the scores!");
    }
}
