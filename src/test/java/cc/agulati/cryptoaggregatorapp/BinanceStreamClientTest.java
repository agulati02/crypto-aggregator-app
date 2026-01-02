package cc.agulati.cryptoaggregatorapp;

import cc.agulati.cryptoaggregatorapp.model.enums.Symbol;
import cc.agulati.cryptoaggregatorapp.service.impl.BinanceStreamClient;
import cc.agulati.cryptoaggregatorapp.utils.BinanceResponseMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

@SpringBootTest
@TestPropertySource(properties = {
        "source.binance.ws.url=wss://stream.binance.com:9443/ws"
})
public class BinanceStreamClientTest {

    private final Logger logger = LoggerFactory.getLogger(BinanceStreamClientTest.class);

    @Autowired
    private BinanceStreamClient binanceStreamClient;

    @Test
    public void testBinanceWebSocketStream() throws InterruptedException {
        this.binanceStreamClient.streamPrices(Symbol.ETH_USD)
                .take(Duration.ofSeconds(5))
                .doOnNext(data -> this.logger.info("Received: {}", data.toString()))
                .doOnError(err -> this.logger.error("Error: {}", err.toString()))
                .doOnComplete(() -> this.logger.info("Stream complete"))
                .blockLast();
    }

}
