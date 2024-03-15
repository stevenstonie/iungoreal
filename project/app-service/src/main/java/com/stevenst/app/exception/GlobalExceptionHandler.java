package com.stevenst.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.stevenst.lib.exception.IgorEntityAlreadyExistsException;
import com.stevenst.lib.exception.IgorEntityNotFoundException;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.payload.ResponsePayload;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ResponseBody
	@ExceptionHandler(IgorEntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponsePayload handleIgorUserNotFoundException(IgorEntityNotFoundException ex) {
		return ResponsePayload.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.message(ex.getMessage())
				.build();
	}

	@ResponseBody
	@ExceptionHandler(IgorEntityAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponsePayload handleIgorUserNotFoundException(IgorEntityAlreadyExistsException ex) {
		return ResponsePayload.builder()
				.status(HttpStatus.CONFLICT.value())
				.message(ex.getMessage())
				.build();
	}

	@ResponseBody
	@ExceptionHandler(IgorPostException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponsePayload handleIgorPostException(IgorPostException ex) {
		return ResponsePayload.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.message(ex.getMessage())
				.build();
	}

	@ResponseBody
	@ExceptionHandler(IgorUserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponsePayload handleIgorUserNotFoundException(IgorUserNotFoundException ex) {
		return ResponsePayload.builder()
				.status(HttpStatus.NOT_FOUND.value())
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
	public ResponsePayload handleMaxSizeException(MaxUploadSizeExceededException exc) {
		return ResponsePayload.builder()
				.status(HttpStatus.PAYLOAD_TOO_LARGE.value())
				.message("File size exceeds maximum allowed limit.")
				.build();
	}
}
