package com.example.sereleasefinalresultservice.exception;

public class ArticleHasBeenContributedException extends RuntimeException {
    private static final long serialVersionUID = -6074753940710869977L;

    public ArticleHasBeenContributedException() {
        super("Article has been contributed!");
    }
}
