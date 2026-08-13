package com.moyeo.backend.global.error;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
