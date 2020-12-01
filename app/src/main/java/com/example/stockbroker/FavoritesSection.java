package com.example.stockbroker;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class FavoritesSection extends Section {
    private Timer timer = new Timer();
    Context c;
    ArrayList<String> favoritesItems;
    public FavoritesSection(ArrayList<String> favoritesList, Context context) {
        super(SectionParameters.builder().itemResourceId(R.layout.favorites_item).build());
        this.c = context;
        this.favoritesItems = favoritesList;
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
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        PortfolioHolder favoritesHolder = (PortfolioHolder) holder;
        favoritesHolder.tickerView.setText(favoritesItems.get(position));
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RequestQueue rq = Volley.newRequestQueue(c);
                String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/details/" + favoritesItems.get(position);
                JsonObjectRequest priceRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject meta = response.getJSONObject("meta");
                            Long currentPrice = data.getLong("last");
                            Long priceChange = data.getLong("last") - data.getLong("prevClose");
                            favoritesHolder.sharesView.setText(meta.getString("name"));
                            favoritesHolder.price.setText(currentPrice.toString());
                            if(priceChange < 0){
                                favoritesHolder.change.setTextColor(Color.RED);
                                favoritesHolder.change.setText("" +Math.abs(priceChange));
                            }
                            else{
                                favoritesHolder.change.setTextColor(Color.GREEN);
                                favoritesHolder.line.setImageResource(R.drawable.ic_twotone_trending_up_24);
                                favoritesHolder.change.setText(priceChange.toString());
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
                rq.add(priceRequest);
            }
        },0,15*1000);
    }
}
