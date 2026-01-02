package cc.agulati.cryptoaggregatorapp.controller;

import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;
import cc.agulati.cryptoaggregatorapp.service.PriceAggregatorService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1")
public class PriceAggregatorController {

    private final PriceAggregatorService priceAggregatorService;

    public PriceAggregatorController(PriceAggregatorService priceAggregatorService) {
        this.priceAggregatorService = priceAggregatorService;
    }

    private Symbol getSymbol(String symbol) {
        switch(symbol) {
            case "BTC-USDT" -> {
                return Symbol.BTC_USD;
            }
            case "ETH-USDT" -> {
                return Symbol.ETH_USD;
            }
            default -> throw new IllegalArgumentException("Couldn't map symbol: " + symbol);
        }
    }

    @GetMapping(path = "/ticker/{symbol}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamPrices(@PathVariable String symbol) {
        AtomicInteger sseId = new AtomicInteger();
        return this.priceAggregatorService.aggregateStreams(this.getSymbol(symbol))
                .map(agg -> ServerSentEvent.builder()
                        .id("" + (sseId.getAndIncrement()))
                        .event("ticker")
                        .data(agg)
                        .build());
    }

}
