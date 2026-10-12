package com.cathay.coin.service;

import com.cathay.coin.client.CoindeskClient;
import com.cathay.coin.config.CoindeskProperties;
import com.cathay.coin.dto.CoindeskResponse;
import com.cathay.coin.dto.TransformedCoindeskResponse;
import java.time.ZoneId;
import org.springframework.stereotype.Service;

@Service
public class CoindeskService {

    private final CoindeskClient client;
    private final CoindeskTransformer transformer;
    private final CurrencyService currencyService;
    private final CoindeskProperties properties;

    public CoindeskService(CoindeskClient client,
                           CoindeskTransformer transformer,
                           CurrencyService currencyService,
                           CoindeskProperties properties) {
        this.client = client;
        this.transformer = transformer;
        this.currencyService = currencyService;
        this.properties = properties;
    }

    public CoindeskResponse fetchRaw() {
        return client.fetch();
    }

    public TransformedCoindeskResponse fetchTransformed() {
        return transformer.transform(
                client.fetch(),
                currencyService.nameByCode(),
                ZoneId.of(properties.getZoneId()));
    }
}
