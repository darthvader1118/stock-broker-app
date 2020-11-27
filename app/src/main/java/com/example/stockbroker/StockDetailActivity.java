package com.example.stockbroker;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class StockDetailActivity extends AppCompatActivity {
    TextView tickerView,name,price,change,portfolio, shares;
    String tag = "detailActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        Intent stockeDetail = getIntent();
        String ticker = stockeDetail.getStringExtra("ticker");
        Log.i(tag,ticker);
        tickerView = (TextView) findViewById(R.id.ticker);
        name = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        change = (TextView) findViewById(R.id.change);
        getStockDetailRequest(ticker);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.star);
        return true;
    }

    public void getStockDetailRequest(String ticker){
        RequestQueue rq = Volley.newRequestQueue(StockDetailActivity.this);
        String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/details/" + ticker;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject details = new JSONObject(response);
                            JSONObject data = details.getJSONObject("data");
                            Log.i(tag, data.toString());
                            JSONObject meta = details.getJSONObject("meta");
                            name.setText(meta.getString("name"));
                            String lastPrice = "$" + data.getString("last");
                            Log.i(tag, lastPrice);
                           price.setText("$" + data.getString("last"));
                            tickerView.setText(data.getString("ticker"));
                            Long changePrice = data.getLong("last") - data.getLong("prevClose");
                            Log.i(tag, changePrice.toString());
                            String changePriceText = "$" + changePrice;
                            change.setText(changePriceText);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(stringRequest);
    }
}