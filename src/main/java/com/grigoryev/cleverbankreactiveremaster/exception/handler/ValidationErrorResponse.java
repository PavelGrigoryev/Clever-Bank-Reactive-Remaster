package com.grigoryev.cleverbankreactiveremaster.exception.handler;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}
