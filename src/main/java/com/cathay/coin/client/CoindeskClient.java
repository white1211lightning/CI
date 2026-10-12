package com.cathay.coin.client;

import com.cathay.coin.config.CoindeskProperties;
import com.cathay.coin.dto.CoindeskResponse;
import com.cathay.coin.exception.CoindeskException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CoindeskClient {

    private final RestTemplate restTemplate;
    private final CoindeskProperties properties;

    public CoindeskClient(RestTemplate restTemplate, CoindeskProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public CoindeskResponse fetch() {
        CoindeskResponse response;
        try {
            response = restTemplate.getForObject(properties.getUrl(), CoindeskResponse.class);
        } catch (RestClientException e) {
            throw new CoindeskException("Failed to call Coindesk API: " + e.getMessage(), e);
        }
        if (response == null) {
            throw new CoindeskException("Coindesk API returned an empty response");
        }
        return response;
    }
}
