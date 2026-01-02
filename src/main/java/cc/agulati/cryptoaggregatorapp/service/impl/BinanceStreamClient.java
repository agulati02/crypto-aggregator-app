package cc.agulati.cryptoaggregatorapp.service.impl;

import cc.agulati.cryptoaggregatorapp.model.dto.BinanceTickerResponse;
import cc.agulati.cryptoaggregatorapp.model.dto.PriceUpdate;
import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;
import cc.agulati.cryptoaggregatorapp.service.StreamClient;
import cc.agulati.cryptoaggregatorapp.utils.BinanceResponseMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Objects;

@Component
public class BinanceStreamClient implements StreamClient {

    @Value("${source.binance.ws.url}")
    private String wsUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BinanceResponseMapper binanceMapper;

    private final Logger logger = LoggerFactory.getLogger(BinanceStreamClient.class);

    public BinanceStreamClient(BinanceResponseMapper binanceMapper) {
        this.binanceMapper = binanceMapper;
    }

    @Override
    public Flux<PriceUpdate> streamPrices(Symbol symbol) {
        return HttpClient.create()
                .websocket()
                .uri(this.wsUrl + "/" + this.parseSymbol(symbol) + "@ticker")
                .handle((inbound, outbound) -> {
                    return inbound.receive()
                            .asString()
                            .flatMap(json -> this.parseJson(json, BinanceTickerResponse.class))
                            .map(this.binanceMapper::toPriceUpdate);
                });
    }

    private String parseSymbol(Symbol symbol) {
        return switch (symbol) {
            case BTC_USD -> "btcusdt";
            case ETH_USD -> "ethusdt";
        };
    }

    private <T> Mono<T> parseJson(String json, Class<T> tClass) {
        return Mono.fromCallable(() -> this.objectMapper.readValue(json, tClass));
    }
}
