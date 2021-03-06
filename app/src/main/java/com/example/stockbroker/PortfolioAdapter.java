package com.example.stockbroker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class PortfolioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Timer timer = new Timer();
    Context c;
    ArrayList<Portfolio> portfolioItems;
    public PortfolioAdapter(Context c, ArrayList<Portfolio> favorites) {
        this.c = c;
        this.portfolioItems = favorites;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_item, null);
        return new PortfolioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DecimalFormat df2 = new DecimalFormat("#.##");
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

    @Override
    public int getItemCount() {
        return portfolioItems.size();
    }
}
