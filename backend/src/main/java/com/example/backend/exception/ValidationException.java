package com.example.backend.exception;

import java.util.Collections;
import java.util.Map;

public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = Collections.emptyMap();
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = (errors != null) ? Map.copyOf(errors) : Collections.emptyMap();
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
