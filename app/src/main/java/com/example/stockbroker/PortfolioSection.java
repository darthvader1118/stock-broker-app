package com.example.stockbroker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;

public class PortfolioSection extends Section {
    private Timer timer = new Timer();
    Context c;
    ArrayList<Portfolio> portfolioItems;
    public PortfolioSection(ArrayList<Portfolio> portfolioList, Context context){

        super(SectionParameters.builder().itemResourceId(R.layout.favorites_item).build());
        this.portfolioItems = portfolioList;
        this.c = context;
    }
    @Override
    public int getContentItemsTotal() {
        return portfolioItems.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new PortfolioHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        PortfolioHolder portfolioHolder = (PortfolioHolder) holder;
        portfolioHolder.tickerView.setText(portfolioItems.get(position).ticker);
        portfolioHolder.sharesView.setText(portfolioItems.get(position).shares.toString() + " shares");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                RequestQueue rq = Volley.newRequestQueue(c);
                String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/details/" + portfolioItems.get(position).ticker;
                JsonObjectRequest priceRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject meta = response.getJSONObject("meta");
                            Long currentPrice = data.getLong("last");
                            Long priceChange = data.getLong("last") - data.getLong("prevClose");
                            portfolioHolder.price.setText(currentPrice.toString());
                            if(priceChange < 0){
                                portfolioHolder.change.setTextColor(Color.RED);
                                portfolioHolder.change.setText("" +Math.abs(priceChange));
                            }
                            else{
                                portfolioHolder.change.setTextColor(Color.GREEN);
                                portfolioHolder.line.setImageResource(R.drawable.ic_twotone_trending_up_24);
                                portfolioHolder.change.setText(priceChange.toString());
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
        }, 0, 15 * 1000);
        portfolioHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(c, StockDetailActivity.class);
                detailIntent.putExtra("ticker", portfolioItems.get(position).ticker);
                c.startActivity(detailIntent);
            }
        });




    }
//    @Override
//    public RecyclerView.ViewHolder getHeaderViewHolder(View view){
//        return new EmptyViewHolder(view);
//    }
}
