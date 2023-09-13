package com.grigoryev.cleverbankreactiveremaster.exception.badrequest;

public class UniquePhoneNumberException extends  BadRequestException {

    public UniquePhoneNumberException(String message) {
        super(message);
    }

}
