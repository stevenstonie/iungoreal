package com.stevenst.app.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.jsonwebtoken.security.SignatureException;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiExceptionFormat handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException.class)
    public ApiExceptionFormat handleBadCredentialsException(BadCredentialsException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(SignatureException.class)
    public ApiExceptionFormat handleSignatureException(SignatureException ex) {
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // -------------------------------------------------------------------

    private ApiExceptionFormat buildResponseEntity(HttpStatus status, String message) {
        ApiExceptionFormat apiException = new ApiExceptionFormat();
        apiException.setStatus(status.value());
        apiException.setMessage(message);
        apiException.setTimestamp(LocalDateTime.now());
        return apiException;
    }
}
