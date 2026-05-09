# Market Activity Tracker

Spring Boot + Thymeleaf web app for exploring public Polymarket activity: trader rankings, whale-sized trades, active whale wallets, and wallet-specific trader profiles.

## Features

- Top trader rankings for weekly, monthly, and six-month windows
- Whale tracker for recent trades above the $10,000 cash threshold
- Active whale detection for wallets with repeated whale trades in a short window
- Trader profile pages backed by wallet-specific public API data
- JSON endpoints for rankings, whale trades, active whales, and trader profiles

Unavailable profile metrics are shown as `N/A` instead of generated or demo values.

## Requirements

- Java 17 or newer
- Maven 3.9+, or the included Maven wrapper

## Run

```bash
./mvnw spring-boot:run
```

Open:

```text
http://localhost:8080/
```

## Main Routes

```text
/                         Web overview
/traders                  Trader rankings page
/trader/{wallet}          Trader profile page
/wallet/search            Wallet search redirect endpoint
/whales/view              Whale tracker page
/traders/top/weekly       Weekly trader JSON
/traders/top/monthly      Monthly trader JSON
/traders/top/half-year    Six-month trader JSON
/trader/{wallet}/data     Trader profile JSON
/whales                   Recent whale trades JSON
/whales/active            Active whale wallets JSON
```

## Build

```bash
./mvnw clean test
./mvnw clean package
```

## Notes

This app uses public Polymarket API endpoints and is intended for educational and portfolio use. It is not financial advice.
