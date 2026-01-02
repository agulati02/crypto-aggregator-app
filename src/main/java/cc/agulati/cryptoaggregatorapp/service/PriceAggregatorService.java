package cc.agulati.cryptoaggregatorapp.service;

import cc.agulati.cryptoaggregatorapp.model.dto.AggregatedPrice;
import cc.agulati.cryptoaggregatorapp.model.dto.PriceUpdate;
import cc.agulati.cryptoaggregatorapp.model.enums.SourceSystem;
import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceAggregatorService {

    private final List<StreamClient> streams;
    private final Logger logger = LoggerFactory.getLogger(PriceAggregatorService.class);
    private final Map<SourceSystem, PriceUpdate> latestPrices = new ConcurrentHashMap<>();

    public PriceAggregatorService(List<StreamClient> streams) {
        this.streams = streams;
    }

    public Flux<AggregatedPrice> aggregateStreams(Symbol symbol) {
        return Flux.merge(
                this.streams.stream()
                        .map(client -> {
                            return client.streamPrices(symbol)
                                    .doOnNext(priceUpdate -> {
                                        this.logger.info("Received {} from {}", priceUpdate.price(), priceUpdate.sourceSystem());
                                    })
                                    .doOnError(err -> {
                                        this.logger.error("Error: {}", err.toString());
                                    });
                        })
                        .toList()
        ).map(priceUpdate -> {
            this.latestPrices.put(priceUpdate.sourceSystem(), priceUpdate);
            List<PriceUpdate> currentPrices = new ArrayList<>(this.latestPrices.values());

            double average = currentPrices.stream()
                    .mapToDouble(PriceUpdate::price)
                    .average()
                    .orElse(0.0);

            double min = currentPrices.stream()
                    .mapToDouble(PriceUpdate::price)
                    .min()
                    .orElse(0.0);

            double max = currentPrices.stream()
                    .mapToDouble(PriceUpdate::price)
                    .max()
                    .orElse(0.0);

            double spread = max - min;

            return new AggregatedPrice(symbol, currentPrices, average, min, max, spread);
        }).doOnNext(agg -> {
            this.logger.info("Aggregated price {}: {} (spread: {})",
                    agg.symbol(), agg.average(), agg.spread());
        });
    }
}
