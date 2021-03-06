package com.example.stockbroker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NewsHolder  extends RecyclerView.ViewHolder {

    ImageView iv;
    TextView source, dateFrom, title;


    public NewsHolder(@NonNull View itemView) {
        super(itemView);

        this.iv = (ImageView) itemView.findViewById(R.id.news_main_img);
        this.source = (TextView) itemView.findViewById(R.id.news_source);
        this.dateFrom = (TextView) itemView.findViewById(R.id.date_from);
        this.title = (TextView) itemView.findViewById(R.id.news_title);
    }
}
