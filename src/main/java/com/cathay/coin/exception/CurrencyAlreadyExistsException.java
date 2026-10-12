package com.cathay.coin.exception;

public class CurrencyAlreadyExistsException extends RuntimeException {

    public CurrencyAlreadyExistsException(String code) {
        super("Currency already exists: " + code);
    }
}
