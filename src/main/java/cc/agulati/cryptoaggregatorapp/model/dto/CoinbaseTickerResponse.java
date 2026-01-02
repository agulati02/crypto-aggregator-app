package cc.agulati.cryptoaggregatorapp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinbaseTickerResponse {

    @JsonProperty("type")
    private String type;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("price")
    private String price;

    @JsonProperty("time")
    private String time;

    public String getType() {
        return type;
    }

    public String getProductId() {
        return productId;
    }

    public String getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
