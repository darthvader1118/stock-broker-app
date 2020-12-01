package com.example.stockbroker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PortfolioHolder extends RecyclerView.ViewHolder{
    ImageView line,arrow;
    TextView tickerView, sharesView, price, change;
    public PortfolioHolder(@NonNull View itemView) {
        super(itemView);
        this.arrow = (ImageView) itemView.findViewById(R.id.arrow);
        this.line =  (ImageView) itemView.findViewById(R.id.chart);
        this.tickerView = (TextView) itemView.findViewById(R.id.item_ticker);
        this.sharesView = (TextView) itemView.findViewById(R.id.subtitle);
        this.price = (TextView) itemView.findViewById(R.id.price_item);
        this.change = (TextView) itemView.findViewById(R.id.change_price);
    }
}
