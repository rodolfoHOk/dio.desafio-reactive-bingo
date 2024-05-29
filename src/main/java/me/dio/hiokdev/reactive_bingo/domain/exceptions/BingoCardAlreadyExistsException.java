package me.dio.hiokdev.reactive_bingo.domain.exceptions;

public class BingoCardAlreadyExistsException extends ReactiveBingoException {

    public BingoCardAlreadyExistsException(String message) {
        super(message);
    }

}
