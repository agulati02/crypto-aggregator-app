package cc.agulati.cryptoaggregatorapp.model.dto;

import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;

import java.util.List;

public record AggregatedPrice(
        Symbol symbol,
        List<PriceUpdate> currentPrices,
        double average,
        double min,
        double max,
        double spread
) {}
