package com.cathay.coin.dto;

import java.util.List;

public class TransformedCoindeskResponse {

    private final String updateTime;
    private final List<CurrencyRate> currencies;

    public TransformedCoindeskResponse(String updateTime, List<CurrencyRate> currencies) {
        this.updateTime = updateTime;
        this.currencies = currencies;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public List<CurrencyRate> getCurrencies() {
        return currencies;
    }
}
