package cc.agulati.cryptoaggregatorapp;

import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;
import cc.agulati.cryptoaggregatorapp.service.PriceAggregatorService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

@SpringBootTest
@TestPropertySource(properties = {
        "source.binance.ws.url=wss://stream.binance.com:9443/ws",
        "source.coinbase.ws.url=wss://ws-feed.exchange.coinbase.com/"
})
public class PriceAggregatorServiceTest {

    @Autowired
    private PriceAggregatorService priceAggregatorService;

    private final Logger logger = LoggerFactory.getLogger(PriceAggregatorServiceTest.class);

    @Test
    public void testAggregateStreams() {
        this.priceAggregatorService.aggregateStreams(Symbol.BTC_USD)
                .take(Duration.ofSeconds(10))
                .doOnError(err -> this.logger.error("Error: {}", err.toString()))
                .doOnComplete(() -> this.logger.info("Stream complete"))
                .blockLast();
    }

}
