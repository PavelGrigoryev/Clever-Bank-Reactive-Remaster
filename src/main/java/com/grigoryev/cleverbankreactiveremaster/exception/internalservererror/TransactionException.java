package com.grigoryev.cleverbankreactiveremaster.exception.internalservererror;

public class TransactionException extends InternalServerErrorException {

    public TransactionException(String message) {
        super(message);
    }

}
