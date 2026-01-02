package cc.agulati.cryptoaggregatorapp.service.impl;

import cc.agulati.cryptoaggregatorapp.model.dto.CoinbaseTickerResponse;
import cc.agulati.cryptoaggregatorapp.model.dto.PriceUpdate;
import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;
import cc.agulati.cryptoaggregatorapp.service.StreamClient;
import cc.agulati.cryptoaggregatorapp.utils.CoinbaseResponseMapper;
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
public class CoinbaseStreamClient implements StreamClient {

    @Value("${source.coinbase.ws.url}")
    private String wsUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CoinbaseResponseMapper coinbaseMapper;

    private final Logger logger = LoggerFactory.getLogger(CoinbaseStreamClient.class);

    public CoinbaseStreamClient(CoinbaseResponseMapper coinbaseMapper) {
        this.coinbaseMapper = coinbaseMapper;
    }

    @Override
    public Flux<PriceUpdate> streamPrices(Symbol symbol) {
        String productId = this.getProductId(symbol);
        String subscriptionPayload = """
                {
                    "type": "subscribe",
                    "product_ids": ["%s"],
                    "channels": ["ticker_batch"]
                }
                """.formatted(productId);

        return HttpClient.create()
                .websocket()
                .uri(this.wsUrl)
                .handle((inbound, outbound) -> {
                    outbound.sendString(Mono.just(subscriptionPayload))
                            .then()
                            .subscribe();

                    return inbound.receive()
                            .asString()
                            .flatMap(json -> this.parseJson(json, CoinbaseTickerResponse.class))
                            .filter(response -> {
                                return Objects.equals(response.getType(), "ticker");
                            })
                            .map(this.coinbaseMapper::toPriceUpdate);
                });

    }

    private String getProductId(Symbol symbol) {
        return switch (symbol) {
            case BTC_USD -> "BTC-USD";
            case ETH_USD -> "ETH-USD";
        };
    }

    private <T> Mono<T> parseJson(String json, Class<T> tClass) {
        return Mono.fromCallable(() -> this.objectMapper.readValue(json, tClass));
    }
}
