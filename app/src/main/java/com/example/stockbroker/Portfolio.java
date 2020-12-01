package com.example.stockbroker;

public class Portfolio {
    String ticker;
    Double cost;
    Long shares;

    public Portfolio(String ticker, Long shares, Double cost) {
        this.ticker = ticker;
        this.shares = shares;
        this.cost = cost;
    }
}
