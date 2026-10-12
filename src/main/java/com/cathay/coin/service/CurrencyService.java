package com.cathay.coin.service;

import com.cathay.coin.dto.CurrencyRequest;
import com.cathay.coin.dto.CurrencyResponse;
import com.cathay.coin.entity.Currency;
import com.cathay.coin.exception.CurrencyAlreadyExistsException;
import com.cathay.coin.exception.CurrencyNotFoundException;
import com.cathay.coin.repository.CurrencyRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CurrencyService {

    private final CurrencyRepository repository;

    public CurrencyService(CurrencyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CurrencyResponse> findAll() {
        return repository.findAllByOrderByCodeAsc().stream()
                .map(CurrencyResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CurrencyResponse findByCode(String code) {
        return CurrencyResponse.from(getEntity(code));
    }

    public CurrencyResponse create(CurrencyRequest request) {
        if (request.getCode() == null) {
            throw new IllegalArgumentException("code must not be null");
        }
        String code = normalize(request.getCode());
        if (repository.existsByCode(code)) {
            throw new CurrencyAlreadyExistsException(code);
        }
        return CurrencyResponse.from(repository.save(new Currency(code, request.getName().trim())));
    }

    public CurrencyResponse update(String code, CurrencyRequest request) {
        Currency currency = getEntity(code);
        currency.setNameZh(request.getName().trim());
        return CurrencyResponse.from(repository.save(currency));
    }

    public void delete(String code) {
        repository.delete(getEntity(code));
    }

    @Transactional(readOnly = true)
    public Map<String, String> nameByCode() {
        Map<String, String> names = new LinkedHashMap<>();
        repository.findAllByOrderByCodeAsc().forEach(c -> names.put(c.getCode(), c.getNameZh()));
        return names;
    }

    private Currency getEntity(String code) {
        String normalized = normalize(code);
        return repository.findByCode(normalized)
                .orElseThrow(() -> new CurrencyNotFoundException(normalized));
    }

    private String normalize(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
