package com.cathay.coin.exception;

public class CoindeskException extends RuntimeException {

    public CoindeskException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoindeskException(String message) {
        super(message);
    }
}
