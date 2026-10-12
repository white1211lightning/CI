package com.cathay.coin.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cathay.coin.config.CoindeskProperties;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@AutoConfigureMockMvc
class CoindeskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CoindeskProperties properties;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    private String fixture() throws IOException {
        return StreamUtils.copyToString(new ClassPathResource("coindesk.json").getInputStream(), StandardCharsets.UTF_8);
    }

    private void expectCoindeskCall(String body) {
        server.expect(requestTo(properties.getUrl()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));
    }

    @Test
    void rawCoindeskApiIsCalledAndReturned() throws Exception {
        expectCoindeskCall(fixture());

        mockMvc.perform(get("/api/coindesk"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chartName", is("Bitcoin")))
                .andExpect(jsonPath("$.time.updatedISO", is("2024-09-02T07:07:20+00:00")))
                .andExpect(jsonPath("$.bpi.USD.rate_float", is(57756.2984)))
                .andExpect(jsonPath("$.bpi.EUR.description", is("Euro")));

        server.verify();
    }

    @Test
    void transformedApiCombinesCoindeskDataWithCurrencyNames() throws Exception {
        expectCoindeskCall(fixture());

        mockMvc.perform(get("/api/coindesk/transformed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updateTime", is("2024/09/02 07:07:20")))
                .andExpect(jsonPath("$.currencies", hasSize(3)))
                .andExpect(jsonPath("$.currencies[0].currency", is("EUR")))
                .andExpect(jsonPath("$.currencies[0].currencyName", is("歐元")))
                .andExpect(jsonPath("$.currencies[0].rate", is(52243.2865)))
                .andExpect(jsonPath("$.currencies[1].currency", is("GBP")))
                .andExpect(jsonPath("$.currencies[1].currencyName", is("英鎊")))
                .andExpect(jsonPath("$.currencies[2].currency", is("USD")))
                .andExpect(jsonPath("$.currencies[2].currencyName", is("美元")))
                .andExpect(jsonPath("$.currencies[2].rate", is(57756.2984)));

        server.verify();
    }

    @Test
    void upstreamFailureIsReportedAsBadGateway() throws Exception {
        server.expect(requestTo(properties.getUrl())).andRespond(withServerError());

        mockMvc.perform(get("/api/coindesk/transformed"))
                .andDo(print())
                .andExpect(status().isBadGateway());
    }
}
