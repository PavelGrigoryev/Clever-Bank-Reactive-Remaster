package com.grigoryev.cleverbankreactiveremaster.exception.notfound;

public class TransactionNotFoundException extends NotFoundException {

    public TransactionNotFoundException(String message) {
        super(message);
    }

}
