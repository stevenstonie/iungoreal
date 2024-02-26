package com.stevenst.lib.exception;

public class IgorUserNotFoundException extends RuntimeException {
    public IgorUserNotFoundException(String message) {
        super(message);
    }
}