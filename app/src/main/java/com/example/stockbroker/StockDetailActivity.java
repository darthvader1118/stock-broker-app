package com.example.stockbroker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StockDetailActivity extends AppCompatActivity {
    TextView tickerView,name,price,change,portfolio, shares;
    GridView statGrid;
    String tag = "detailActivity";
    boolean isSaved = false;
    public static final String WATCHLIST_FILE = "watchlist";
    public static final String PORFTFOLIO_FILE = "portfolio";
    JSONObject stats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_StockBroker);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        Intent stockDetail = getIntent();
        String ticker = stockDetail.getStringExtra("ticker");
        Log.i(tag,ticker);
        tickerView = (TextView) findViewById(R.id.ticker);
        name = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        change = (TextView) findViewById(R.id.change);
        shares = (TextView) findViewById(R.id.shares);
        getStockDetailRequest(ticker);
        getPortfolioAmount();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.star);
        SharedPreferences watchlist = getSharedPreferences(WATCHLIST_FILE,  MODE_PRIVATE);
        String ticker =  getIntent().getStringExtra("ticker").toLowerCase();
        String storedValue = watchlist.getString(ticker, "didn't work");
        Log.i(tag, "storedValue is: " + storedValue);
        if(storedValue.toLowerCase().equals(ticker.toLowerCase())){
            menuItem.setIcon(R.drawable.ic_baseline_star_24);
            isSaved = true;
        }
//        menuItem.setOnMenuItemClickListener(starToggle);
        Log.i(tag,"menu added");
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.star){
            if(isSaved){
                item.setIcon(R.drawable.ic_baseline_star_border_24);
                removeFromWatchlist();
            }
            else{
                item.setIcon(R.drawable.ic_baseline_star_24);
                saveToWatchlist();
            }
            isSaved = !isSaved;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveToWatchlist(){
        SharedPreferences watchlist = getSharedPreferences(WATCHLIST_FILE,  MODE_PRIVATE);
        SharedPreferences.Editor editor = watchlist.edit();
        String ticker =  tickerView.getText().toString();
        Log.i(tag, ticker + " saved to watchlist");
        editor.putString(ticker.toLowerCase(), ticker);
        editor.apply();
        Toast.makeText(StockDetailActivity.this,"saved to watchlist", Toast.LENGTH_SHORT).show();
    }

    public void removeFromWatchlist(){
        SharedPreferences watchlist = getSharedPreferences(WATCHLIST_FILE,  MODE_PRIVATE);
        SharedPreferences.Editor editor = watchlist.edit();
        String ticker =  tickerView.getText().toString().toLowerCase();
        Log.i(tag, ticker + " removed from watchlist");
        editor.remove(ticker);
        editor.apply();
        Toast.makeText(StockDetailActivity.this,"removed from watchlist", Toast.LENGTH_SHORT).show();
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
                            if(changePrice < 0){
                                change.setTextColor(Color.RED);
                            }
                            else{
                                change.setTextColor(Color.GREEN);
                            }
                            Log.i(tag, changePrice.toString());
                            String changePriceText = "$" + changePrice;
                            change.setText(changePriceText);
                            setStatGrid(data);
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

    public void getPortfolioAmount(){
        SharedPreferences portfolio = getSharedPreferences(PORFTFOLIO_FILE, MODE_PRIVATE);
        int shareAmt = portfolio.getInt("shares", 0);
        if(shareAmt == 0){
            Log.i(tag, "you have 0 shares of " + getIntent().getStringExtra("ticker").toUpperCase() + " Start trading!");
            shares.setText("you have 0 shares of " + getIntent().getStringExtra("ticker").toUpperCase() + " Start trading!");
        }
        else{
            shares.setText("Shares owned: " + shareAmt + " Market value: " + shareAmt*Integer.parseInt(price.getText().toString()));
        }
    }

    public void setStatGrid(JSONObject stats) throws JSONException {
        statGrid = (GridView) findViewById(R.id.statsContent);
        statGrid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }
        });
        ArrayList<String> statItems = new ArrayList<>();
        statItems.add("Current Price: "  +stats.getString("last"));
        statItems.add("Low: " + stats.getString("low"));
        statItems.add("Bid Price: " + stats.getString("bidPrice"));
        statItems.add("Open Price: " + stats.getString("open"));
        statItems.add("Mid: " + stats.getString("mid"));
        statItems.add("High: " + stats.getString("high"));
        statItems.add("Volume: " + stats.getString("volume"));

        ArrayAdapter<String> statGridAdapter = new ArrayAdapter<String>(StockDetailActivity.this, android.R.layout.simple_list_item_1, statItems);
        statGrid.setAdapter(statGridAdapter);

    }


}