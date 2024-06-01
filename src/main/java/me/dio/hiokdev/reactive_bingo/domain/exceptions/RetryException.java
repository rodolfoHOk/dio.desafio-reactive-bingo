package me.dio.hiokdev.reactive_bingo.domain.exceptions;

public class RetryException extends ReactiveBingoException {

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }

}
