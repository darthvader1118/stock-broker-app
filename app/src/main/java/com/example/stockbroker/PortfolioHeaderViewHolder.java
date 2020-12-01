package com.example.stockbroker;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PortfolioHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView header;
    public PortfolioHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.portfolio_title2);
    }
}
