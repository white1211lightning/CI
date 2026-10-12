package com.cathay.coin.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static String json(String code, String name) {
        return code == null
                ? "{\"name\":\"" + name + "\"}"
                : "{\"code\":\"" + code + "\",\"name\":\"" + name + "\"}";
    }

    @Test
    void listReturnsSeededCurrenciesOrderedByCode() throws Exception {
        mockMvc.perform(get("/api/currencies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].code", is("EUR")))
                .andExpect(jsonPath("$[0].name", is("歐元")));
    }

    @Test
    void getReturnsSingleCurrencyIgnoringCase() throws Exception {
        mockMvc.perform(get("/api/currencies/usd"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("USD")))
                .andExpect(jsonPath("$.name", is("美元")));
    }

    @Test
    void getUnknownCurrencyReturns404() throws Exception {
        mockMvc.perform(get("/api/currencies/XXX"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void createAddsCurrency() throws Exception {
        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("cny", "人民幣").getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("CNY")))
                .andExpect(jsonPath("$.name", is("人民幣")));

        mockMvc.perform(get("/api/currencies"))
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    void createDuplicateReturns409() throws Exception {
        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("USD", "美金").getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void createWithInvalidPayloadReturns400() throws Exception {
        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("TOOLONG", "").getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(null, "無代碼").getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateChangesChineseName() throws Exception {
        mockMvc.perform(put("/api/currencies/USD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(null, "美金").getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("USD")))
                .andExpect(jsonPath("$.name", is("美金")));

        mockMvc.perform(get("/api/currencies/USD"))
                .andDo(print())
                .andExpect(jsonPath("$.name", is("美金")));
    }

    @Test
    void updateUnknownCurrencyReturns404() throws Exception {
        mockMvc.perform(put("/api/currencies/XXX")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(null, "不存在").getBytes(StandardCharsets.UTF_8)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRemovesCurrency() throws Exception {
        mockMvc.perform(delete("/api/currencies/USD"))
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/currencies/USD"))
                .andDo(print())
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/currencies"))
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    void deleteUnknownCurrencyReturns404() throws Exception {
        mockMvc.perform(delete("/api/currencies/XXX"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
