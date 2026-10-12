package com.cathay.coin.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CurrencyRequest {

    @Pattern(regexp = "^[A-Za-z]{3}$", message = "code must be a 3-letter currency code")
    private String code;

    @NotBlank(message = "name must not be blank")
    @Size(max = 50, message = "name must not exceed 50 characters")
    private String name;

    public CurrencyRequest() {
    }

    public CurrencyRequest(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
