package me.dio.hiokdev.reactive_bingo.domain.exceptions;

public class RoundAlreadyInitiatedException extends ReactiveBingoException {

    public RoundAlreadyInitiatedException(String message) {
        super(message);
    }

}
