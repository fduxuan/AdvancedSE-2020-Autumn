package com.example.sereleasefinalresultservice.exception;

public class NoDiscussionException extends RuntimeException {
    public NoDiscussionException() {
        super("You must post your opinion in the discussion first!");
    }
}
