package com.clicks.secured_qr_backend.exceptions;

public class InvalidParamsException extends RuntimeException{
    public InvalidParamsException(String message) {
        super(message);
    }
}
