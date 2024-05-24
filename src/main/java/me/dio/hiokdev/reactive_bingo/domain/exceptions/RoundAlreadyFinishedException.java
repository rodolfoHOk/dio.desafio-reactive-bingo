package me.dio.hiokdev.reactive_bingo.domain.exceptions;

public class RoundAlreadyFinishedException extends ReactiveBingoException {

    public RoundAlreadyFinishedException(String message) {
        super(message);
    }

}
