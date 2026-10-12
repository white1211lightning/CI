package com.cathay.coin.controller;

import com.cathay.coin.dto.CurrencyRequest;
import com.cathay.coin.dto.CurrencyResponse;
import com.cathay.coin.service.CurrencyService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencyService service;

    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping
    public List<CurrencyResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{code}")
    public CurrencyResponse get(@PathVariable String code) {
        return service.findByCode(code);
    }

    @PostMapping
    public ResponseEntity<CurrencyResponse> create(@Valid @RequestBody CurrencyRequest request) {
        CurrencyResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/currencies/" + created.getCode())).body(created);
    }

    @PutMapping("/{code}")
    public CurrencyResponse update(@PathVariable String code, @Valid @RequestBody CurrencyRequest request) {
        return service.update(code, request);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
