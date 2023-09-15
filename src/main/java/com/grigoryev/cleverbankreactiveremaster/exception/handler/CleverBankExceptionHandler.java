package com.grigoryev.cleverbankreactiveremaster.exception.handler;

import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.BadRequestException;
import com.grigoryev.cleverbankreactiveremaster.exception.internalservererror.InternalServerErrorException;
import com.grigoryev.cleverbankreactiveremaster.exception.notfound.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;

@RestControllerAdvice
public class CleverBankExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ValidationErrorResponse> handleWebExchangeBindException(WebExchangeBindException e) {
        List<Violation> violations = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new Violation(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationErrorResponse(violations));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintValidationException(ConstraintViolationException e) {
        List<Violation> violations = e.getConstraintViolations()
                .stream()
                .map(constraintViolation -> new Violation(constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationErrorResponse(violations));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ExceptionResponse> handleInternalServerErrorException(InternalServerErrorException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse(e.getMessage()));
    }

}
