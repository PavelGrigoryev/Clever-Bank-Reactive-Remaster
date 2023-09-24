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
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
public class CleverBankExceptionHandler {

    private final String className = this.getClass().getName();

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ValidationErrorResponse>> handleWebExchangeBindException(WebExchangeBindException e) {
        List<Violation> violations = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new Violation(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationErrorResponse(violations)))
                .log(className);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ValidationErrorResponse>> handleConstraintValidationException(ConstraintViolationException e) {
        List<Violation> violations = e.getConstraintViolations()
                .stream()
                .map(constraintViolation -> new Violation(constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage()))
                .toList();
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationErrorResponse(violations)))
                .log(className);
    }

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleNotFoundException(NotFoundException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(e.getMessage())))
                .log(className);
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleBadRequestException(BadRequestException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(e.getMessage())))
                .log(className);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleInternalServerErrorException(InternalServerErrorException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse(e.getMessage())))
                .log(className);
    }

}
