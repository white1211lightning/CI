package com.cathay.coin.dto;

import com.cathay.coin.entity.Currency;

public class CurrencyResponse {

    private final Long id;
    private final String code;
    private final String name;

    public CurrencyResponse(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static CurrencyResponse from(Currency currency) {
        return new CurrencyResponse(currency.getId(), currency.getCode(), currency.getNameZh());
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
