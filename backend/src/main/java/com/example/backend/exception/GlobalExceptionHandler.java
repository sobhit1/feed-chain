package com.example.backend.exception;

import com.example.backend.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Global exception handler that produces ApiErrorResponse DTOs for all handled
 * exceptions.
 * Returns consistent, typed JSON payloads so the frontend can parse errors
 * reliably.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${spring.profiles.active:dev}")
    private String appEnv;

    private ApiErrorResponse buildResponse(HttpServletRequest req,
            HttpStatus status,
            String message,
            Map<String, String> details) {

        String traceId = MDC.get("traceId");

        Map<String, String> enrichedDetails = details != null
                ? new HashMap<>(details)
                : new HashMap<>();

        if (traceId != null && !traceId.isBlank()) {
            enrichedDetails.put("traceId", traceId);
        }

        Map<String, String> finalDetails = Collections
                .unmodifiableMap(enrichedDetails.isEmpty() ? null : enrichedDetails);

        return new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                finalDetails);
    }

    // ----------------- Custom Exception Handlers -----------------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String traceId = MDC.get("traceId");

        log.warn("[{} {}] {} - {} - traceId={}",
                req.getMethod(),
                req.getRemoteAddr(),
                req.getRequestURI(),
                ex.getMessage(),
                traceId != null ? traceId : "N/A");

        ApiErrorResponse body = buildResponse(req, status, ex.getMessage(), null);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String traceId = MDC.get("traceId");

        log.warn("[{} {}] {} - {} - traceId={}",
                req.getMethod(),
                req.getRemoteAddr(),
                req.getRequestURI(),
                ex.getMessage(),
                traceId != null ? traceId : "N/A");

        ApiErrorResponse body = buildResponse(req, status, ex.getMessage(), null);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(ValidationException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String traceId = MDC.get("traceId");

        log.warn("[{} {}] {} - {} - details={} - traceId={}",
                req.getMethod(),
                req.getRemoteAddr(),
                req.getRequestURI(),
                ex.getMessage(),
                ex.getErrors(),
                traceId != null ? traceId : "N/A");

        ApiErrorResponse body = buildResponse(req, status, ex.getMessage(),
                ex.getErrors().isEmpty() ? null : ex.getErrors());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String traceId = MDC.get("traceId");

        log.warn("[{} {}] {} - {} - traceId={}",
                req.getMethod(),
                req.getRemoteAddr(),
                req.getRequestURI(),
                ex.getMessage(),
                traceId != null ? traceId : "N/A");

        ApiErrorResponse body = buildResponse(req, status, ex.getMessage(), null);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String traceId = MDC.get("traceId");

        log.warn("[{} {}] {} - {} - traceId={}",
                req.getMethod(),
                req.getRemoteAddr(),
                req.getRequestURI(),
                ex.getMessage(),
                traceId != null ? traceId : "N/A");

        ApiErrorResponse body = buildResponse(req, status, ex.getMessage(), null);
        return new ResponseEntity<>(body, status);
    }

    // ----------------- Validation & Bind Handlers -----------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex,
            HttpServletRequest req) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String traceId = MDC.get("traceId");

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (existing, _) -> existing));

        log.warn("[{} {}] {} - MethodArgumentNotValid: {} - traceId={}",
                req.getMethod(),
                req.getRemoteAddr(),
                req.getRequestURI(),
                fieldErrors,
                traceId != null ? traceId : "N/A");

        ApiErrorResponse body = buildResponse(req, status, "Validation failed", fieldErrors);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse> handleBindException(BindException ex, HttpServletRequest req) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        String traceId = MDC.get("traceId");

        Map<String, String> fieldErrors = ex.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (existing, _) -> existing));

        log.warn("[{} {}] {} - Bind/validation errors: {} - traceId={}",
                req.getMethod(),
                req.getRemoteAddr(),
                req.getRequestURI(),
                fieldErrors,
                traceId != null ? traceId : "N/A");

        ApiErrorResponse body = buildResponse(req, status, "Validation failed", fieldErrors);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
            HttpServletRequest req) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String traceId = MDC.get("traceId");

        Map<String, String> violations = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> Optional.ofNullable(v.getPropertyPath()).map(Object::toString).orElse("unknown"),
                        ConstraintViolation::getMessage,
                        (existing, _) -> existing));

        log.warn("[{} {}] {} - Constraint violations: {} - traceId={}",
                req.getMethod(),
                req.getRemoteAddr(),
                req.getRequestURI(),
                violations,
                traceId != null ? traceId : "N/A");

        ApiErrorResponse body = buildResponse(req, status, "Constraint validation failed", violations);
        return new ResponseEntity<>(body, status);
    }

    // ----------------- Generic / Fallback Handler -----------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, String> details = null;
        String traceId = MDC.get("traceId");

        if ("dev".equalsIgnoreCase(appEnv)) {
            String trace = Arrays.stream(ex.getStackTrace())
                    .limit(5)
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("\n"));
            details = new HashMap<>();
            details.put("exception", ex.getClass().getName());
            details.put("message", ex.getMessage());
            details.put("trace", trace);
        }

        log.error("[{} {}] {} - Unhandled exception: {} - traceId={}",
                req.getMethod(), req.getRemoteAddr(), req.getRequestURI(),
                ex.getMessage(), traceId != null ? traceId : "N/A", ex);

        ApiErrorResponse body = buildResponse(req, status, "An unexpected error occurred", details);
        return new ResponseEntity<>(body, status);
    }
}
