package com.grigoryev.cleverbankreactiveremaster.exception.badrequest;

public class AccountClosedException extends BadRequestException {

    public AccountClosedException(String message) {
        super(message);
    }

}
