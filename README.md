# Coin API

Spring Boot 2.7 / Maven / JDK 8 (compiled with `--release 8`) / H2 / Spring Data JPA.

## Run

```bash
mvn spring-boot:run      # http://localhost:8080
mvn test                 # runs all tests; MockMvc prints request/response contents
```

H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:coindb`, user `sa`, empty password).

## Database

`src/main/resources/schema.sql` creates the `CURRENCY` table and `src/main/resources/data.sql` seeds it
(USD, GBP, EUR, TWD, JPY). Both are executed automatically on startup.

```sql
CREATE TABLE IF NOT EXISTS CURRENCY (
    ID      BIGINT AUTO_INCREMENT PRIMARY KEY,
    CODE    VARCHAR(10) NOT NULL,
    NAME_ZH VARCHAR(50) NOT NULL,
    CONSTRAINT UK_CURRENCY_CODE UNIQUE (CODE)
);
```

## API

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/currencies` | List currencies (ordered by code) |
| GET | `/api/currencies/{code}` | Get one currency |
| POST | `/api/currencies` | Create, body `{"code":"CNY","name":"人民幣"}` (201, 409 if exists) |
| PUT | `/api/currencies/{code}` | Update Chinese name, body `{"name":"美金"}` |
| DELETE | `/api/currencies/{code}` | Delete (204) |
| GET | `/api/coindesk` | Calls the Coindesk API and returns its content |
| GET | `/api/coindesk/transformed` | Calls Coindesk and returns the transformed content |

Transformed response example:

```json
{
  "updateTime": "2024/09/02 07:07:20",
  "currencies": [
    {"currency": "EUR", "currencyName": "歐元", "rate": 52243.2865},
    {"currency": "GBP", "currencyName": "英鎊", "rate": 43984.0203},
    {"currency": "USD", "currencyName": "美元", "rate": 57756.2984}
  ]
}
```

Configuration (`application.properties`): `app.coindesk.url`, `app.coindesk.zone-id` (time zone used to format
`updateTime`, default `UTC`), and connect/read timeouts. Upstream failures return HTTP 502.

## Tests

- `CoindeskTransformerTest` - unit tests for the transformation logic.
- `CurrencyControllerTest` - CRUD API tests (prints responses).
- `CoindeskControllerTest` - Coindesk API call and transformed API tests (upstream is mocked with
  `MockRestServiceServer` using `src/test/resources/coindesk.json`, so tests do not need network access).
