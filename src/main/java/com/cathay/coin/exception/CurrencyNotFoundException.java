package com.cathay.coin.exception;

public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(String code) {
        super("Currency not found: " + code);
    }
}
