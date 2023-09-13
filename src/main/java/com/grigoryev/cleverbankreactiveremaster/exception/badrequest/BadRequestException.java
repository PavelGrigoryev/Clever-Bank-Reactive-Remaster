package com.grigoryev.cleverbankreactiveremaster.exception.badrequest;

public abstract class BadRequestException extends RuntimeException {

    protected BadRequestException(String message) {
        super(message);
    }

}
