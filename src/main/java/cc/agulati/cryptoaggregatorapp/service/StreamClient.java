package cc.agulati.cryptoaggregatorapp.service;

import cc.agulati.cryptoaggregatorapp.model.dto.PriceUpdate;
import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;
import reactor.core.publisher.Flux;

public interface StreamClient {

    public Flux<PriceUpdate> streamPrices(Symbol symbol);

}