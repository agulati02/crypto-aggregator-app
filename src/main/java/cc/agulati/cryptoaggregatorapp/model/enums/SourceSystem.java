package cc.agulati.cryptoaggregatorapp.model.enums;

public enum SourceSystem {
    BINANCE("BINANCE"),
    COINBASE("COINBASE");

    public final String label;

    private SourceSystem(String label) {
        this.label = label;
    }
}
