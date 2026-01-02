package cc.agulati.cryptoaggregatorapp.utils;

import cc.agulati.cryptoaggregatorapp.model.dto.CoinbaseTickerResponse;
import cc.agulati.cryptoaggregatorapp.model.dto.PriceUpdate;
import cc.agulati.cryptoaggregatorapp.model.enums.SourceSystem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class CoinbaseResponseMapper implements ExchangeMapper<CoinbaseTickerResponse> {
    @Override
    public PriceUpdate toPriceUpdate(CoinbaseTickerResponse response) {
        String symbol = this.extractSymbol(response.getProductId());
        double price = Double.parseDouble(response.getPrice());
        Instant timestamp = Instant.parse(response.getTime());

        return new PriceUpdate(SourceSystem.COINBASE, symbol, price, timestamp);
    }

    private String extractSymbol(String pair) {
        return pair.split("-")[0];
    }
}
