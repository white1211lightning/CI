package com.cathay.coin.repository;

import com.cathay.coin.entity.Currency;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByCode(String code);

    boolean existsByCode(String code);

    List<Currency> findAllByOrderByCodeAsc();
}
