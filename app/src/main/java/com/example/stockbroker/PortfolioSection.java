package com.example.stockbroker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.utils.EmptyViewHolder;
//import io.github.luizgrp.sectionedrecyclerviewadapter.R;

public class PortfolioSection extends Section {

    private RequestQueue rq;
    Context c;
    ArrayList<Portfolio> portfolioItems;
//    private final ClickListener clickListener;
    public PortfolioSection(ArrayList<Portfolio> portfolioList, Context context, RequestQueue rq){

        super(SectionParameters.builder().itemResourceId(R.layout.favorites_item)
//                .headerResourceId(R.layout.portfolio_header)
                .build());
        this.portfolioItems = portfolioList;
        this.c = context;
        this.rq = rq;

//        this.clickListener = clickListener;
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
        DecimalFormat df2 = new DecimalFormat("#.##");
        PortfolioHolder portfolioHolder = (PortfolioHolder) holder;
        portfolioHolder.tickerView.setText(portfolioItems.get(position).ticker);
        portfolioHolder.sharesView.setText(portfolioItems.get(position).shares.toString() + " shares");
        String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/details/" + portfolioItems.get(position).ticker;
        portfolioHolder.resetTimer();
        portfolioHolder.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {



                JsonObjectRequest priceRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(c.getPackageName(),response.toString());
                            JSONObject data = response.getJSONObject("data");
                            JSONObject meta = response.getJSONObject("meta");
                            Double currentPrice = data.getDouble("last");
                            Double priceChange = data.getDouble("last") - data.getDouble("prevClose");
                            portfolioHolder.price.setText(df2.format(currentPrice));
                            if(priceChange < 0){
                                portfolioHolder.change.setTextColor(Color.RED);
                                portfolioHolder.change.setText("" +df2.format(Math.abs(priceChange)));
                            }
                            else{
                                portfolioHolder.change.setTextColor(Color.GREEN);
                                portfolioHolder.line.setImageResource(R.drawable.ic_twotone_trending_up_24);
                                portfolioHolder.change.setText(df2.format(priceChange));
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
        }, 0, 15 * 1000);
//        , @NonNull final ClickListener clickListener
//        portfolioHolder.rootView.setOnClickListener(v ->
//                clickListener.onItemRootViewClicked(this, portfolioHolder.getAdapterPosition()));
        portfolioHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(c, StockDetailActivity.class);
                detailIntent.putExtra("ticker", portfolioItems.get(position).ticker);
                c.startActivity(detailIntent);
            }
        });




    }

 //   @Override
//    public RecyclerView.ViewHolder getHeaderViewHolder(View view){
//        return new EmptyViewHolder(view);
//    }
//    @Override
//    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
//        final PortfolioHeaderViewHolder headerHolder = (PortfolioHeaderViewHolder) holder;
//
//        headerHolder.header.setText("Portfolio");
//    }

}
