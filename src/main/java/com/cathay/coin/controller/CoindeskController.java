package com.cathay.coin.controller;

import com.cathay.coin.dto.CoindeskResponse;
import com.cathay.coin.dto.TransformedCoindeskResponse;
import com.cathay.coin.service.CoindeskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coindesk")
public class CoindeskController {

    private final CoindeskService service;

    public CoindeskController(CoindeskService service) {
        this.service = service;
    }

    @GetMapping
    public CoindeskResponse raw() {
        return service.fetchRaw();
    }

    @GetMapping("/transformed")
    public TransformedCoindeskResponse transformed() {
        return service.fetchTransformed();
    }
}
