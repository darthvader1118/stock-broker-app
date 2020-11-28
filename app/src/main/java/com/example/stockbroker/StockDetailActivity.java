package com.example.stockbroker;

import android.content.Intent;
import android.content.SharedPreferences;
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
    boolean isSaved = false;
    public static final String WATCHLIST_FILE = "watchlist";
    public static final String PORFTFOLIO_FILE = "portfolio";
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
        getStockDetailRequest(ticker);

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

//    MenuItem.OnMenuItemClickListener starToggle = new MenuItem.OnMenuItemClickListener() {
//        @Override
//        public boolean onMenuItemClick(MenuItem item) {
//            Log.i(tag,"clicked");
//            if(isSaved) {
//                item.setIcon(R.drawable.ic_baseline_star_24);
//            }else{
//                item.setIcon(R.drawable.ic_baseline_star_border_24);
//            }
//            return false;
//        }
//    };

//    public void onClickStar(){
//        Log.i(tag,"clicked");
//        if(isSaved) {
//            item.setIcon(R.drawable.ic_baseline_star_24);
//        }else{
//            item.setIcon(R.drawable.ic_baseline_star_border_24);
//        }
//    }


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
    }

    public void removeFromWatchlist(){
        SharedPreferences watchlist = getSharedPreferences(WATCHLIST_FILE,  MODE_PRIVATE);
        SharedPreferences.Editor editor = watchlist.edit();
        String ticker =  tickerView.getText().toString().toLowerCase();
        Log.i(tag, ticker + " removed from watchlist");
        editor.remove(ticker);
        editor.apply();
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