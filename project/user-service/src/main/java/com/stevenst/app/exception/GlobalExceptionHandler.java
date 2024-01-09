package com.stevenst.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.stevenst.lib.payload.ResponsePayload;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(IgorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponsePayload handleIgorNotFoundException(IgorNotFoundException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(IgorFriendRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponsePayload handleIgorFriendRequestException(IgorFriendRequestException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }
}
