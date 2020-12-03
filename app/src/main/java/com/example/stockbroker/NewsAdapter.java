package com.example.stockbroker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case 0:
                NewsHolder newsHolder = (NewsHolder) holder;
                newsHolder.title.setText(newsItems.get(position).title);
                newsHolder.source.setText(newsItems.get(position).source);
                newsHolder.dateFrom.setText(newsItems.get(position).dateFrom);
                Picasso.get().load(newsItems.get(position).imageUri).resize(407,167).into(newsHolder.iv);
                newsHolder.iv.setClipToOutline(true);
                newsHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog newsDialog = new Dialog(c);
                        newsDialog.setContentView(R.layout.news_dialog);
                        ImageView ivDialog = newsDialog.findViewById(R.id.newsImage);
                        Picasso.get().load(newsItems.get(position).imageUri).into(ivDialog);
                        TextView dialogTitle = newsDialog.findViewById(R.id.dialog_title);
                        dialogTitle.setText(newsItems.get(position).title);
                        ImageView chrome = newsDialog.findViewById(R.id.chrome);
                        chrome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(newsItems.get(position).url));
                                c.startActivity(i);
                            }
                        });
                        ImageView tweet = newsDialog.findViewById(R.id.twitter);
                        tweet.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("https://twitter.com/intent/tweet?url="+newsItems.get(position).url));
                                c.startActivity(i);
                            }
                        });
                        newsDialog.show();
                    }
                });

                break;
            case 2:
                NewsHolder2 newsHolder2 = (NewsHolder2) holder;
                newsHolder2.title.setText(newsItems.get(position).title);
                newsHolder2.source.setText(newsItems.get(position).source);
                newsHolder2.dateFrom.setText(newsItems.get(position).dateFrom);
                Picasso.get().load(newsItems.get(position).imageUri).resize(165,179).into(newsHolder2.iv);
                newsHolder2.iv.setClipToOutline(true);
                newsHolder2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog newsDialog = new Dialog(c);
                        newsDialog.setContentView(R.layout.news_dialog);
                        ImageView ivDialog = newsDialog.findViewById(R.id.newsImage);
                        TextView dialogTitle = newsDialog.findViewById(R.id.dialog_title);
                        dialogTitle.setText(newsItems.get(position).title);
                        Picasso.get().load(newsItems.get(position).imageUri).into(ivDialog);
                        ImageView chrome = newsDialog.findViewById(R.id.chrome);
                        chrome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(newsItems.get(position).url));
                                c.startActivity(i);
                            }
                        });
                        ImageView tweet = newsDialog.findViewById(R.id.twitter);
                        tweet.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("https://twitter.com/intent/tweet?url="+newsItems.get(position).url));
                                c.startActivity(i);
                            }
                        });
                        newsDialog.show();
                    }
                });
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
