package org.core.dnd_ai.global;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ProblemDetail>> handleConstrainViolation(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(e.getBindingResult().getAllErrors().stream()
                        .map(error -> {
                            var problemDetail =
                                    ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.getDefaultMessage());
                            problemDetail.setTitle("Constraint Violation");
                            return problemDetail;
                        })
                        .toList());
    }
}
