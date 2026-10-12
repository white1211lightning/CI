package com.cathay.coin.dto;

import java.math.BigDecimal;

public class CurrencyRate {

    private final String currency;
    private final String currencyName;
    private final BigDecimal rate;

    public CurrencyRate(String currency, String currencyName, BigDecimal rate) {
        this.currency = currency;
        this.currencyName = currencyName;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
