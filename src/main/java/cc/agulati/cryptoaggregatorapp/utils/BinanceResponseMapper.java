package cc.agulati.cryptoaggregatorapp.utils;

import cc.agulati.cryptoaggregatorapp.model.dto.BinanceTickerResponse;
import cc.agulati.cryptoaggregatorapp.model.dto.PriceUpdate;
import cc.agulati.cryptoaggregatorapp.model.enums.SourceSystem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
public class BinanceResponseMapper implements ExchangeMapper<BinanceTickerResponse> {
    @Override
    public PriceUpdate toPriceUpdate(BinanceTickerResponse response) {
        String symbol = this.extractSymbol(response.getSymbol());
        double price = Double.parseDouble(response.getLastPrice());
        Instant timestamp = Instant.ofEpochMilli(response.getEventTime());

        return new PriceUpdate(SourceSystem.BINANCE, symbol, price, timestamp);
    }

    private String extractSymbol(String pair) {
        return pair.replace("USDT", "");
    }
}
