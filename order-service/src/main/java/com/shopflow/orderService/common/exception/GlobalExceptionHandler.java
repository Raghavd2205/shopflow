package com.shopflow.orderService.common.exception;

import com.shopflow.orderService.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ─── 404 Not Found ──────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        logger.error("Resource not found: {}", ex.getMessage());
        return new ErrorResponse(404, ex.getMessage(),
                "Not Found", request.getRequestURI());
    }

    // ─── 409 Conflict ───────────────────────────
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        return new ErrorResponse(409, ex.getMessage(),
                "Conflict", request.getRequestURI());
    }

    // ─── 400 Validation Error ───────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ErrorResponse(400, errors.toString(),
                "Bad Request", request.getRequestURI());
    }

    // ─── 400 Bad Request ────────────────────────
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return new ErrorResponse(400, ex.getMessage(),
                "Bad Request", request.getRequestURI());
    }

    // ─── 500 Internal Server Error ──────────────
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        logger.error("Unhandled exception: ", ex);
        return new ErrorResponse(500, "Internal server error",
                "Internal Server Error", request.getRequestURI());
    }
}