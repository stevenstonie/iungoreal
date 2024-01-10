package com.stevenst.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.stevenst.lib.payload.ResponsePayload;

@ControllerAdvice
public class GlobalExceptionHandler {
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
				.message("File size exceeds maximum allowed limit." + exc.getMessage())
				.build();
	}
}
