package cc.agulati.cryptoaggregatorapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceTickerResponse {

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("c")
    private String lastPrice;

    @JsonProperty("E")
    private long eventTime;

    public String getSymbol() {
        return symbol;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
}
