package com.example.stockbroker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Timer;

public class PortfolioHolder extends RecyclerView.ViewHolder{
    final View rootView;
    ImageView line,arrow;
    TextView tickerView, sharesView, price, change;
    Timer timer;
    public PortfolioHolder(@NonNull View itemView) {
        super(itemView);
        this.rootView = itemView;
        this.arrow = (ImageView) itemView.findViewById(R.id.arrow);
        this.line =  (ImageView) itemView.findViewById(R.id.chart);
        this.tickerView = (TextView) itemView.findViewById(R.id.item_ticker);
        this.sharesView = (TextView) itemView.findViewById(R.id.subtitle);
        this.price = (TextView) itemView.findViewById(R.id.price_item);
        this.change = (TextView) itemView.findViewById(R.id.change_price);
    }

    public void resetTimer(){
        if(timer != null) {
            destroyTimer();
            timer = new Timer();
        }
        else{
            timer = new Timer();
        }
    }
    public void destroyTimer(){
        if(timer != null) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
    }

    public String getSharesView() {
        return sharesView.getText().toString();
    }

    public Double getPrice() {
        return  Double.parseDouble(price.getText().toString());
    }
}
