package com.stevenst.app.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ApiExceptionFormat {
    private LocalDateTime timestamp;
    private int status;
    private String message;
}
