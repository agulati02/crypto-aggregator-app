package cc.agulati.cryptoaggregatorapp.model.dto;

import cc.agulati.cryptoaggregatorapp.model.enums.SourceSystem;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceUpdate(SourceSystem sourceSystem, String symbol, double price, Instant timestamp) {

}
