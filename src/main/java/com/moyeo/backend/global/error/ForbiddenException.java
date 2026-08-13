package com.moyeo.backend.global.error;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
