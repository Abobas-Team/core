package org.core.dnd_ai.global;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.core.dnd_ai.global.exception.EmailAlreadyExistsException;
import org.core.dnd_ai.global.exception.UsernameAlreadyExistsException;
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

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException e) {
        String message = Optional.ofNullable(e)
                .map(Throwable::getMessage)
                // Expected NullPointerException, thrown from @NonNull lombok annotation
                .filter(msg -> msg.endsWith("is marked non-null but is null"))
                // Unexpected NullPointerException
                .orElse(null);

        return message != null
                ? ResponseEntity.badRequest().body(message)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
