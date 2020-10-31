package com.example.scdiscussionservice.exception;

public class NoDiscussionException extends RuntimeException {
    public NoDiscussionException() {
        super("You must post your opinion in the discussion first!");
    }
}
