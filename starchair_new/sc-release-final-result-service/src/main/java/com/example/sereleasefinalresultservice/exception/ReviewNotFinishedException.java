package com.example.sereleasefinalresultservice.exception;

public class ReviewNotFinishedException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public ReviewNotFinishedException() {
        super("There are still articles unreviewed, please waiting until finishes.");
    }
}
