package com.grigoryev.cleverbankreactiveremaster.exception.internalservererror;

public class JsonParseException extends InternalServerErrorException {

    public JsonParseException(String message) {
        super(message);
    }

}
