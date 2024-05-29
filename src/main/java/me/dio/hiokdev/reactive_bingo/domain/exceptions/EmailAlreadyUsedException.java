package me.dio.hiokdev.reactive_bingo.domain.exceptions;

public class EmailAlreadyUsedException extends ReactiveBingoException {

    public EmailAlreadyUsedException(String message) {
        super(message);
    }

}
