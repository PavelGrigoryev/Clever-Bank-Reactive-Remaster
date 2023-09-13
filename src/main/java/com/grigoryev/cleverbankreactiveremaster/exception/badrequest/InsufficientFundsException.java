package com.grigoryev.cleverbankreactiveremaster.exception.badrequest;

public class InsufficientFundsException extends BadRequestException {

    public InsufficientFundsException(String message) {
        super(message);
    }

}
