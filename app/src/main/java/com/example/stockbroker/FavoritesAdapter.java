package com.example.stockbroker;

import android.content.Context;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Timer timer = new Timer();
    Context c;
    ArrayList<String> favoritesItems;
    public FavoritesAdapter(Context c, ArrayList<String> favorites) {
        this.c = c;
        this.favoritesItems = favorites;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_item, null);
        return new PortfolioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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

    @Override
    public int getItemCount() {
        return favoritesItems.size();
    }
}
