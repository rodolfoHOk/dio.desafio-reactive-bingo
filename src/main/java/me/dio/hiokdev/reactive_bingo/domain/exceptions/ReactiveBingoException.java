package me.dio.hiokdev.reactive_bingo.domain.exceptions;

public class ReactiveBingoException extends RuntimeException {

    public ReactiveBingoException(String message) {
        super(message);
    }

    public ReactiveBingoException(String message, Throwable cause) {
        super(message, cause);
    }

}
