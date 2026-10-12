package com.cathay.coin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cathay.coin.dto.CoindeskResponse;
import com.cathay.coin.dto.CurrencyRate;
import com.cathay.coin.dto.TransformedCoindeskResponse;
import com.cathay.coin.exception.CoindeskException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CoindeskTransformerTest {

    private final CoindeskTransformer transformer = new CoindeskTransformer();
    private final ObjectMapper mapper = new ObjectMapper();

    private static Map<String, String> names() {
        Map<String, String> names = new HashMap<>();
        names.put("USD", "美元");
        names.put("GBP", "英鎊");
        names.put("EUR", "歐元");
        return names;
    }

    private CoindeskResponse sample() throws Exception {
        return mapper.readValue(getClass().getResourceAsStream("/coindesk.json"), CoindeskResponse.class);
    }

    @Test
    void formatsUpdateTimeAsUtc() {
        assertEquals("2024/09/02 07:07:20",
                transformer.formatTime("2024-09-02T07:07:20+00:00", ZoneId.of("UTC")));
    }

    @Test
    void formatsUpdateTimeInConfiguredZone() {
        assertEquals("2024/09/02 15:07:20",
                transformer.formatTime("2024-09-02T07:07:20+00:00", ZoneId.of("Asia/Taipei")));
    }

    @Test
    void rejectsInvalidTime() {
        assertThrows(CoindeskException.class, () -> transformer.formatTime("not-a-time", ZoneId.of("UTC")));
        assertThrows(CoindeskException.class, () -> transformer.formatTime(null, ZoneId.of("UTC")));
    }

    @Test
    void transformsCoindeskPayload() throws Exception {
        TransformedCoindeskResponse result = transformer.transform(sample(), names(), ZoneId.of("UTC"));

        assertEquals("2024/09/02 07:07:20", result.getUpdateTime());
        assertEquals(3, result.getCurrencies().size());

        CurrencyRate eur = result.getCurrencies().get(0);
        assertEquals("EUR", eur.getCurrency());
        assertEquals("歐元", eur.getCurrencyName());
        assertEquals(0, new BigDecimal("52243.2865").compareTo(eur.getRate()));

        CurrencyRate gbp = result.getCurrencies().get(1);
        assertEquals("GBP", gbp.getCurrency());
        assertEquals("英鎊", gbp.getCurrencyName());

        CurrencyRate usd = result.getCurrencies().get(2);
        assertEquals("USD", usd.getCurrency());
        assertEquals("美元", usd.getCurrencyName());
        assertEquals(0, new BigDecimal("57756.2984").compareTo(usd.getRate()));
    }

    @Test
    void leavesNameEmptyWhenCurrencyIsNotMapped() throws Exception {
        Map<String, String> names = names();
        names.remove("GBP");

        TransformedCoindeskResponse result = transformer.transform(sample(), names, ZoneId.of("UTC"));

        CurrencyRate gbp = result.getCurrencies().get(1);
        assertEquals("GBP", gbp.getCurrency());
        assertNull(gbp.getCurrencyName());
    }

    @Test
    void failsWhenTimeIsMissing() {
        assertThrows(CoindeskException.class,
                () -> transformer.transform(new CoindeskResponse(), names(), ZoneId.of("UTC")));
    }
}
