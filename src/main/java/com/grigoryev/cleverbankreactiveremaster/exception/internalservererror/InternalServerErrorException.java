package com.grigoryev.cleverbankreactiveremaster.exception.internalservererror;

public abstract class InternalServerErrorException extends RuntimeException {

    protected InternalServerErrorException(String message) {
        super(message);
    }

}
