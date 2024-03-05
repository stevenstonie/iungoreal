package com.stevenst.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.stevenst.lib.exception.IgorEmptyFileNameException;
import com.stevenst.lib.exception.IgorEntityNotFoundException;
import com.stevenst.lib.exception.IgorImageNotFoundException;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.exception.IgorNullValueException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.payload.ResponsePayload;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(IgorUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponsePayload handleIgorNotFoundException(IgorUserNotFoundException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(IgorEntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponsePayload handleIgorEntityNotFoundException(IgorEntityNotFoundException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(IgorNullValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponsePayload handleIgorNullPointerException(IgorNullValueException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.BAD_REQUEST.value())
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

    @ResponseBody
    @ExceptionHandler(IgorEmptyFileNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponsePayload handleIgorEmptyFileNameException(IgorEmptyFileNameException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(IgorIoException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponsePayload handleIgorIoException(IgorIoException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponsePayload handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .message(ex.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(IgorImageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponsePayload handleIgorImageNotFoundException(IgorImageNotFoundException ex) {
        return ResponsePayload.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
    }
}
