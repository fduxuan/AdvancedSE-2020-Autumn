package com.example.scexaminemanuscripts.exception;

public class NoDiscussionException extends RuntimeException {
    public NoDiscussionException() {
        super("You must post your opinion in the discussion first!");
    }
}
