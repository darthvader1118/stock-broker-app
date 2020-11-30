package com.example.stockbroker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    class NewsHolder2 extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView source, dateFrom, title;
        public NewsHolder2(@NonNull View itemView) {
            super(itemView);
            this.iv = (ImageView) itemView.findViewById(R.id.imageView2);
            this.source = (TextView) itemView.findViewById(R.id.source_side);
            this.dateFrom = (TextView) itemView.findViewById(R.id.date_from_side);
            this.title = (TextView) itemView.findViewById(R.id.title_side);
        }
    }

    Context c;
    ArrayList<NewsItem> newsItems;

    public NewsAdapter(Context c, ArrayList<NewsItem> newsItems) {
        this.c = c;
        this.newsItems = newsItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_main_card, null);
                return new NewsHolder(view);
            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_side_card, null);
                return new NewsHolder2(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case 0:
                NewsHolder newsHolder = (NewsHolder) holder;
                newsHolder.title.setText(newsItems.get(position).title);
                newsHolder.source.setText(newsItems.get(position).source);
                newsHolder.dateFrom.setText(newsItems.get(position).dateFrom);
                Picasso.get().load(newsItems.get(position).imageUri).resize(407,167).into(newsHolder.iv);

                break;
            case 2:
                NewsHolder2 newsHolder2 = (NewsHolder2) holder;
                newsHolder2.title.setText(newsItems.get(position).title);
                newsHolder2.source.setText(newsItems.get(position).source);
                newsHolder2.dateFrom.setText(newsItems.get(position).dateFrom);
                Picasso.get().load(newsItems.get(position).imageUri).resize(165,179).into(newsHolder2.iv);
                break;
        }

    }



    @Override
    public int getItemCount() {
        return this.newsItems.size();
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return 0;
        }
        else {
            return 2;
        }
    }

}
