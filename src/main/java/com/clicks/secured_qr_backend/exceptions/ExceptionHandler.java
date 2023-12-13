package com.clicks.secured_qr_backend.exceptions;

import com.clicks.secured_qr_backend.dtos.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseStatus
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new CustomResponse(false, exception.getMessage())
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<CustomResponse> handleEntityExists(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new CustomResponse(false, exception.getMessage())
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidParamsException.class)
    public ResponseEntity<CustomResponse> badRequest(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new CustomResponse(false, exception.getMessage())
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<CustomResponse> unauthorized(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new CustomResponse(false, exception.getMessage())
        );
    }

}
