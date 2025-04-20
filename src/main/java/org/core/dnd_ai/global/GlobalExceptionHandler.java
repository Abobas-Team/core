package org.core.dnd_ai.global;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.core.dnd_ai.global.exception.EmailAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private String formatFieldErrorMessage(FieldError fieldError) {
        return String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ProblemDetail>> handleConstrainViolation(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(e.getBindingResult().getAllErrors().stream()
                        .map(error -> {
                            var detail = error instanceof FieldError fieldError
                                    ? ProblemDetail.forStatusAndDetail(
                                            HttpStatus.BAD_REQUEST, formatFieldErrorMessage(fieldError))
                                    : ProblemDetail.forStatusAndDetail(
                                            HttpStatus.BAD_REQUEST, error.getDefaultMessage());
                            detail.setTitle("Constraint Violation");
                            return detail;
                        })
                        .toList());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
