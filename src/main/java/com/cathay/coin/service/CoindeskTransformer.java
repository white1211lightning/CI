package com.cathay.coin.service;

import com.cathay.coin.dto.CoindeskResponse;
import com.cathay.coin.dto.CurrencyRate;
import com.cathay.coin.dto.TransformedCoindeskResponse;
import com.cathay.coin.exception.CoindeskException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Pure transformation logic: Coindesk payload + currency name mapping -> new API payload.
 */
@Component
public class CoindeskTransformer {

    static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public TransformedCoindeskResponse transform(CoindeskResponse source,
                                                 Map<String, String> currencyNames,
                                                 ZoneId zoneId) {
        if (source == null || source.getTime() == null) {
            throw new CoindeskException("Coindesk response does not contain time information");
        }
        String updateTime = formatTime(source.getTime().getUpdatedISO(), zoneId);

        List<CurrencyRate> rates = new ArrayList<>();
        if (source.getBpi() != null) {
            for (Map.Entry<String, CoindeskResponse.Bpi> entry : source.getBpi().entrySet()) {
                CoindeskResponse.Bpi bpi = entry.getValue();
                String code = bpi != null && bpi.getCode() != null ? bpi.getCode() : entry.getKey();
                rates.add(new CurrencyRate(code, currencyNames.get(code), bpi == null ? null : bpi.getRateFloat()));
            }
        }
        rates.sort((a, b) -> a.getCurrency().compareTo(b.getCurrency()));
        return new TransformedCoindeskResponse(updateTime, rates);
    }

    public String formatTime(String isoTime, ZoneId zoneId) {
        if (isoTime == null || isoTime.trim().isEmpty()) {
            throw new CoindeskException("Coindesk response does not contain updatedISO");
        }
        try {
            return OffsetDateTime.parse(isoTime).atZoneSameInstant(zoneId).format(OUTPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new CoindeskException("Invalid updatedISO value: " + isoTime, e);
        }
    }
}
