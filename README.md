# Crypto Price Aggregator Application
<hr />

### Overview

This is a mini project that demonstrates the use of Server-Sent Events (SSE) and how reactive programming (Project Reactor / WebFlux) helps building and composing streaming data flows. The application aggregates cryptocurrency price updates from multiple exchange stream clients, computes simple aggregates (average, min, max, spread) and exposes a live SSE endpoint that streams aggregated prices to connected clients.

## What this project demonstrates

- A minimal, pragmatic SSE endpoint implemented with Spring WebFlux that streams events to HTTP clients.
- How reactive streams (Flux) make it easy to compose multiple real-time sources (websocket-based exchange streams) and transform them into a single streaming output.
- Mapping of raw exchange-specific payloads into a common domain record (`PriceUpdate`) and producing aggregated domain messages (`AggregatedPrice`).

## Key knowledge points

- SSE endpoint
	- The controller `cc.agulati.cryptoaggregatorapp.controller.PriceAggregatorController` exposes an SSE endpoint at `/api/v1/ticker/{symbol}` and returns a `Flux<ServerSentEvent<?>>` with `MediaType.TEXT_EVENT_STREAM_VALUE`.
	- Each event is built with an incrementing id (`AtomicInteger`) and an event name of `ticker`.

- Reactive composition and aggregation
	- `cc.agulati.cryptoaggregatorapp.service.PriceAggregatorService#aggregateStreams` merges multiple `Flux<PriceUpdate>` streams using `Flux.merge(...)` and maps incoming `PriceUpdate` values into an `AggregatedPrice` record.
	- Reactive operators used in the service include `map`, `doOnNext`, and `doOnError` for non-blocking transformation and side-effect logging.
	- The service maintains a concurrent map (`ConcurrentHashMap`) of latest prices per source and computes average / min / max / spread on each incoming update.

- Stream client implementation
	- The `StreamClient` interface defines `Flux<PriceUpdate> streamPrices(Symbol symbol)` allowing multiple implementations to provide price streams.
	- `cc.agulati.cryptoaggregatorapp.service.impl.BinanceStreamClient` and `...CoinbaseStreamClient` use Reactor Netty's `HttpClient.websocket()` to connect to exchange websocket endpoints and transform inbound raw JSON to typed DTOs and then to `PriceUpdate` via mapper classes.
	- Coinbase client demonstrates sending an outbound subscription payload after opening the websocket connection; Binance uses a ticker websocket path.

- Mapping and DTOs
	- Exchange-specific DTOs (`BinanceTickerResponse`, `CoinbaseTickerResponse`) are converted to the common `PriceUpdate` record using `BinanceResponseMapper` and `CoinbaseResponseMapper` which normalize fields (symbol extraction, timestamp parsing, price parsing).
	- Aggregated results are represented by the `AggregatedPrice` record containing the symbol, list of current prices, average, min, max and spread.

- Error handling and runtime behavior
	- The code logs incoming updates and errors via `doOnNext` and `doOnError` but does not terminate the merged stream on a single upstream error — each source can fail independently.
	- Backpressure is inherited from Reactor types; however, the sample code does not add explicit backpressure strategies (e.g., onBackpressureLatest or buffering limits) between websocket inbound and SSE output. This is a useful area for improvement in production systems.

- Practical trade-offs highlighted by this project
	- Using reactive streams keeps the IO non-blocking and composable — you can merge many sources without spawning thread-per-connection.
	- Keeping an in-memory `latestPrices` map is simple but stateful; for multi-instance deployments you would use an external shared store or a stateless aggregation approach.
	- The example focuses on clarity over robustness: it demonstrates the mechanics (websocket -> DTO -> domain -> aggregated SSE) while leaving production concerns (retries, reconnection strategies, backpressure tuning, metrics, and resilience patterns) as next steps.

## How to try the SSE endpoint

- Start the application (typical Spring Boot run via Maven):

	mvn spring-boot:run

- Open the SSE stream for BTC (example):

	curl -N http://localhost:8080/api/v1/ticker/BTC-USDT

The `-N` flag tells curl not to buffer the output so you can see events as they arrive.

## Files of interest

- `src/main/java/cc/agulati/cryptoaggregatorapp/controller/PriceAggregatorController.java` — SSE controller / endpoint
- `src/main/java/cc/agulati/cryptoaggregatorapp/service/PriceAggregatorService.java` — merges streams and computes aggregates
- `src/main/java/cc/agulati/cryptoaggregatorapp/service/StreamClient.java` — abstraction for exchange stream producers
- `src/main/java/cc/agulati/cryptoaggregatorapp/service/impl/*` — exchange-specific stream client implementations
- `src/main/java/cc/agulati/cryptoaggregatorapp/utils/*ResponseMapper.java` — mapping exchange DTOs to `PriceUpdate`

## Before you take this to production...

- Add explicit backpressure and buffering strategies between inbound websocket streams and SSE output.
- Add reconnection and retry logic for upstream websocket clients.
- Add integration tests that simulate websocket feeds and verify SSE consumer behavior.

---

This README aims to highlight the learning goals and the concrete places in the code where those concepts are implemented.
