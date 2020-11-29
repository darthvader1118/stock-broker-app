package com.example.stockbroker;

public class Portfolio {
    String ticker;
    Long cost;
    Long shares;

    public Portfolio(String ticker, Long shares, Long cost) {
        this.ticker = ticker;
        this.shares = shares;
        this.cost = cost;
    }
}
