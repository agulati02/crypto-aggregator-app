package cc.agulati.cryptoaggregatorapp.utils;

import cc.agulati.cryptoaggregatorapp.model.dto.PriceUpdate;

public interface ExchangeMapper<T> {
    PriceUpdate toPriceUpdate(T exchangeResponse);
}
