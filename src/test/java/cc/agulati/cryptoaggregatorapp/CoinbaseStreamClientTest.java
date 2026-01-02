package cc.agulati.cryptoaggregatorapp;

import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;
import cc.agulati.cryptoaggregatorapp.service.impl.CoinbaseStreamClient;
import cc.agulati.cryptoaggregatorapp.utils.CoinbaseResponseMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

@SpringBootTest
@TestPropertySource(properties = {
        "source.coinbase.ws.url=wss://ws-feed.exchange.coinbase.com/"
})
public class CoinbaseStreamClientTest {

    private final Logger logger = LoggerFactory.getLogger(CoinbaseStreamClientTest.class);

    @Autowired
    private CoinbaseStreamClient coinbaseStreamClient;

    @Test
    public void testCoinbaseWebSocketStream() throws InterruptedException {
        this.coinbaseStreamClient.streamPrices(Symbol.BTC_USD)
                .take(Duration.ofSeconds(11))
                .doOnNext(data -> this.logger.info("Received: {}", data.toString()))
                .doOnError(err -> this.logger.error("Error: {}", err.toString()))
                .doOnComplete(() -> this.logger.info("Stream complete"))
                .blockLast();
    }

}
