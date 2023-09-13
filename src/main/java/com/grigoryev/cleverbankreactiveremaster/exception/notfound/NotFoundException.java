package com.grigoryev.cleverbankreactiveremaster.exception.notfound;

public abstract class NotFoundException extends RuntimeException {

    protected NotFoundException(String message) {
        super(message);
    }

}
