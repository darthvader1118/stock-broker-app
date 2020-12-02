package com.example.stockbroker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;

public class FavoritesSection extends Section {
    private Timer timer;
    Context c;
    ArrayList<String> favoritesItems;
    private RequestQueue rq;
    public void setFavoritesItems(ArrayList<String> favoritesItems) {
        this.favoritesItems = favoritesItems;
    }

    public FavoritesSection(ArrayList<String> favoritesList, Context context, RequestQueue rq, Timer timer) {
        super(SectionParameters.builder().itemResourceId(R.layout.favorites_item)
           //    .headerResourceId(R.layout.favorites_header)
                .build());
        this.c = context;
        this.favoritesItems = favoritesList;
        this.rq = rq;
        this.timer = timer;
    }

    @Override
    public int getContentItemsTotal() {
        return favoritesItems.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new PortfolioHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder,final int position) {

        DecimalFormat df2 = new DecimalFormat("#.##");
        final PortfolioHolder favoritesHolder = (PortfolioHolder) holder;
        favoritesHolder.tickerView.setText(favoritesItems.get(position));
        String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/details/" + favoritesItems.get(position);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int pos = position;
                RequestQueue rq = Volley.newRequestQueue(c);

                JsonObjectRequest priceRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject meta = response.getJSONObject("meta");
                            Double currentPrice = data.getDouble("last");
                            Double priceChange = data.getDouble("last") - data.getDouble("prevClose");
                            favoritesHolder.sharesView.setText(meta.getString("name"));
                            favoritesHolder.price.setText(df2.format(currentPrice));
                            if(priceChange < 0){
                                favoritesHolder.change.setTextColor(Color.RED);
                                favoritesHolder.change.setText("" +df2.format(Math.abs(priceChange)));
                            }
                            else{
                                favoritesHolder.change.setTextColor(Color.GREEN);
                                favoritesHolder.line.setImageResource(R.drawable.ic_twotone_trending_up_24);
                                favoritesHolder.change.setText(df2.format(priceChange));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                priceRequest.setTag("item");
                rq.add(priceRequest);
            }
        },0,15*1000);
        favoritesHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(c, StockDetailActivity.class);
                detailIntent.putExtra("ticker", favoritesItems.get(position));
                c.startActivity(detailIntent);
            }
        });
    }
//    @Override
//    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
//        // return an empty instance of ViewHolder for the headers of this section
//        return new EmptyViewHolder(view);
//    }
 public void removeItem(int position) {
     favoritesItems.remove(position);
 }
}
