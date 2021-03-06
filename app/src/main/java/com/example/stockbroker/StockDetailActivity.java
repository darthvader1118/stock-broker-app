package com.example.stockbroker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.DateInterval;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.time.*;
import java.time.temporal.ChronoUnit;

public class StockDetailActivity extends AppCompatActivity {
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    ConstraintLayout constraintLayout;
    ProgressBar spinner2;
    TextView fetching;
    private ProgressBar spinner;
    TextView tickerView,name,price,change,portfolio, shares, about, showMore;
    GridView statGrid;
    WebView wv;
    String tag = "detailActivity";
    boolean isSaved = false;
    private Timer timer = new Timer();
    public static final String WATCHLIST_FILE = "watchlist";
    public static final String PORFTFOLIO_FILE = "portfolio";
    public static final String reqTag2 = "Detail request";
    JSONObject stats;
    JSONObject priceData;
    RequestQueue rq;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_StockBroker);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
//        constraintLayout = (ConstraintLayout) findViewById(R.id.stock_details);
//        constraintLayout.setVisibility(View.GONE);
        rq = Volley.newRequestQueue(StockDetailActivity.this);
        Intent stockDetail = getIntent();
        String ticker = stockDetail.getStringExtra("ticker");
        Log.i(tag,ticker);
        spinner2=(ProgressBar)findViewById(R.id.spinner_detail);
        fetching = (TextView) findViewById(R.id.fetching);
        constraintLayout = (ConstraintLayout) findViewById(R.id.stock_details);
        spinner2.setVisibility(View.VISIBLE);
        fetching.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.GONE);
        wv = (WebView) findViewById(R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.clearCache(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("file:///android_asset/chart.html?ticker=" + ticker);
        tickerView = (TextView) findViewById(R.id.ticker);
        name = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        change = (TextView) findViewById(R.id.change);
        shares = (TextView) findViewById(R.id.shares);
        getStockDetailRequest(ticker);

        about = (TextView) findViewById(R.id.aboutContent);
        getNewsItems(ticker);
        getHistoricalData(ticker);





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
        if(id == R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveToWatchlist(){
        SharedPreferences watchlist = getSharedPreferences(WATCHLIST_FILE,  MODE_PRIVATE);
        SharedPreferences.Editor editor = watchlist.edit();
        String ticker =  tickerView.getText().toString();
        Log.i(tag, ticker + " saved to watchlist");
        editor.putString(ticker.toLowerCase(), ticker);
        editor.apply();
        Toast.makeText(StockDetailActivity.this,ticker + " saved to favorites", Toast.LENGTH_SHORT).show();
    }

    public void removeFromWatchlist(){
        SharedPreferences watchlist = getSharedPreferences(WATCHLIST_FILE,  MODE_PRIVATE);
        SharedPreferences.Editor editor = watchlist.edit();
        String ticker =  tickerView.getText().toString().toLowerCase();
        Log.i(tag, ticker + " removed from watchlist");
        editor.remove(ticker);
        editor.apply();
        Toast.makeText(StockDetailActivity.this,ticker.toUpperCase() + " removed from favorites", Toast.LENGTH_SHORT).show();
    }

    public void getStockDetailRequest(String ticker){


        String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/details/" + ticker;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject details = new JSONObject(response);
                            JSONObject data = details.getJSONObject("data");
                            priceData = data;
                            Log.i(tag, data.toString());
                            JSONObject meta = details.getJSONObject("meta");
                            stats = meta;
                            name.setText(meta.getString("name"));
                            String lastPrice = "$" + data.getString("last");
                            Log.i(tag, lastPrice);
                           price.setText("$" + df2.format(data.getDouble("last")));
                            tickerView.setText(data.getString("ticker"));
                            Log.i(tag, "Long " + data.getDouble("last"));
                            Double changePrice = data.getDouble("last") - data.getDouble("prevClose");
                            if(changePrice < 0){
                                change.setTextColor(Color.RED);
                            }
                            else{
                                change.setTextColor(Color.GREEN);
                            }
                            Log.i(tag, changePrice.toString());
                            String changePriceText = "$" + df2.format(Math.abs(changePrice));
                            change.setText(changePriceText);
                            getPortfolioAmount(data.getString("ticker"));
                            setStatGrid(data);
                            about.setText(meta.getString("description"));
                            constraintLayout.setVisibility(View.VISIBLE);
                            spinner2.setVisibility(View.GONE);
                            fetching.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setTag(reqTag2);
        rq.add(stringRequest);
    }

    public void getPortfolioAmount(String ticker){
        SharedPreferences portfolio = getSharedPreferences(PORFTFOLIO_FILE, MODE_PRIVATE);
         String portfolioJson = portfolio.getString(ticker, "");
         if(portfolioJson== ""){
             Portfolio portfolio1 = new Portfolio(ticker, new Long(0),new Double(0));
             Log.i(tag, "you have 0 shares of " + getIntent().getStringExtra("ticker").toUpperCase() + " Start trading!");
             shares.setText("you have 0 shares of " + getIntent().getStringExtra("ticker").toUpperCase() + " Start trading!");
         }else{
             Gson gson = new Gson();
             Portfolio portfolio1 = gson.fromJson(portfolioJson,Portfolio.class);
             long shareAmt = portfolio1.shares;
             if(shareAmt == 0){
                 Log.i(tag, "you have 0 shares of " + getIntent().getStringExtra("ticker").toUpperCase() + " Start trading!");
                 shares.setText("you have 0 shares of " + getIntent().getStringExtra("ticker").toUpperCase() + " Start trading!");
             }
             else{
                 String priceAmt = price.getText().toString().substring(1);
                 shares.setText("Shares owned: " + shareAmt + " Market value: " + df2.format(shareAmt*Double.parseDouble(priceAmt)));
                 //
             }
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



    public void aboutMore(View view) {
        showMore = (TextView) findViewById(R.id.showMore);
        if(showMore.getText().toString().equals("Show More...")) {
            about.setMaxLines(Integer.MAX_VALUE);
            about.setEllipsize(null);
            showMore.setText("Show Less");
        }
        else{
            about.setMaxLines(3);
            about.setEllipsize(TextUtils.TruncateAt.END);
            showMore.setText("Show More...");
        }
    }

    public void onClickTrade(View view) throws JSONException {
        final Dialog tradeDialog = new Dialog(StockDetailActivity.this);
        tradeDialog.setContentView(R.layout.trade_dialog);
        tradeDialog.setTitle("Trade");
        TextView header = (TextView) tradeDialog.findViewById(R.id.trade_title);
        header.setText("Trade " + stats.getString("name") + " shares");
        TextView shareTotal = (TextView) tradeDialog.findViewById(R.id.total);
        EditText number = (EditText) tradeDialog.findViewById(R.id.share_number);
        TextView cashTotal = (TextView) tradeDialog.findViewById(R.id.total_amount);
        SharedPreferences portfolio = getSharedPreferences(PORFTFOLIO_FILE,MODE_PRIVATE);
        float cash = portfolio.getFloat("cash", 20000);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        cashTotal.setText("$" + decimalFormat.format(cash) + " available to buy " + stats.getString("ticker"));
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                try {
//                    shareTotal.setText( "0 x $" + priceData.getLong("last") + "/share = $" + Integer.parseInt(number.getText().toString())*priceData.getLong("last"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(s.toString().equals("")){
                        shareTotal.setText( "0 x$" + priceData.getDouble("last") + "/share = $" + Integer.parseInt("0")*priceData.getDouble("last"));
                    }else {
                        Double shareNum = Double.parseDouble(number.getText().toString());
                        Double total = shareNum * priceData.getDouble("last");
                        String sum = s.toString() + "x$" + priceData.getDouble("last") + "/share = $" + total;
                        shareTotal.setText(sum);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button buy = (Button) tradeDialog.findViewById(R.id.buy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences portfolio = getSharedPreferences(PORFTFOLIO_FILE, MODE_PRIVATE);
                SharedPreferences.Editor editPortfolio = portfolio.edit();
                Long shares = Long.parseLong(number.getText().toString());
                try {
                    String dialogTicker = stats.getString("ticker");
                    Double shareTot = priceData.getDouble("last")*shares;
                    float cash = portfolio.getFloat("cash", 20000);
                    cash = cash - Float.parseFloat(shareTot.toString());
                    if(cash < 0){
                        Toast.makeText(StockDetailActivity.this,"Not enough money to buy", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(number.getText().toString()) <= 0){
                        Toast.makeText(StockDetailActivity.this,"Cannot buy less than 0 shares", Toast.LENGTH_SHORT).show();
                    }
                    else if(number.getText().toString().contains("[a-zA-Z]+")){
                        Toast.makeText(StockDetailActivity.this,"please enter valid amount", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Gson gson = new Gson();
                        Portfolio newItem  = new Portfolio(dialogTicker,Double.doubleToLongBits(0.0),0.0);
                        Portfolio item = gson.fromJson(portfolio.getString(dialogTicker, gson.toJson(newItem)),Portfolio.class);
                        Long newShares = item.shares + shares;
                        Double newCost = item.cost + shares*priceData.getDouble("last");
                        editPortfolio.putString(stats.getString("ticker"), gson.toJson(new Portfolio(stats.getString("ticker"), newShares, newCost)));
                        editPortfolio.putFloat("cash", cash);
                        editPortfolio.apply();
                        tradeDialog.dismiss();
                        final Dialog successDialog = new Dialog(StockDetailActivity.this);
                        successDialog.setContentView(R.layout.success_dialog);
                        TextView message = (TextView) successDialog.findViewById(R.id.success_message);
                        message.setText("You have successfully bought " + shares+ " shares of " + dialogTicker);
                        Button done = (Button) successDialog.findViewById(R.id.done);
                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                successDialog.dismiss();
                                try {
                                    getPortfolioAmount(stats.getString("ticker"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        successDialog.show();;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        Button sell = (Button) tradeDialog.findViewById(R.id.sell);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences portfolio = getSharedPreferences(PORFTFOLIO_FILE, MODE_PRIVATE);
                SharedPreferences.Editor editPortfolio = portfolio.edit();
                Gson gson = new Gson();

                Long shares = Long.parseLong(number.getText().toString());
                try {
                    String dialogTicker = stats.getString("ticker");
                    Portfolio newItem  = new Portfolio(dialogTicker,Double.doubleToLongBits(0.0),0.0);
                    String stockData = portfolio.getString(dialogTicker, gson.toJson(newItem));
                    Portfolio portfolioItem = gson.fromJson(stockData, Portfolio.class);
                    Long currentShares = portfolioItem.shares;



                    if(currentShares < shares){
                        Toast.makeText(StockDetailActivity.this,"Not enough shares to sell", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(number.getText().toString()) <= 0){
                        Toast.makeText(StockDetailActivity.this,"Cannot sell less than 0 shares", Toast.LENGTH_SHORT).show();
                    }
                    else if(number.getText().toString().contains("[a-zA-Z]+")){
                        Toast.makeText(StockDetailActivity.this,"please enter valid amount", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        float cash = portfolio.getFloat("cash", 20000);
                        Double shareTot = priceData.getDouble("last") * shares;
                        cash = cash + shareTot.floatValue();
                        currentShares = currentShares - shares;
                        Double cost = portfolioItem.cost  - portfolioItem.cost/portfolioItem.shares*shares;
                        editPortfolio.putFloat("cash", cash);
                        if(currentShares.compareTo(new Long(0)) == 0){
                            editPortfolio.remove(dialogTicker);
                            editPortfolio.commit();
                        }
                        else {
                            editPortfolio.putString(dialogTicker, gson.toJson(new Portfolio(dialogTicker,currentShares,cost)));
                            editPortfolio.apply();
                        }
                        tradeDialog.dismiss();
                        final Dialog successDialog = new Dialog(StockDetailActivity.this);
                        successDialog.setContentView(R.layout.success_dialog);
                        TextView message = (TextView) successDialog.findViewById(R.id.success_message);
                        message.setText("You have successfully sold " + shares+ " shares of " + dialogTicker);
                        Button done = (Button) successDialog.findViewById(R.id.done);
                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                successDialog.dismiss();
                                try {
                                    getPortfolioAmount(stats.getString("ticker"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        successDialog.show();;

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });


        tradeDialog.show();
        Window window = tradeDialog.getWindow();
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);



    }

    public void getNewsItems(String ticker){
        RecyclerView rv = (RecyclerView) findViewById(R.id.newsList);
        rv.setLayoutManager(new LinearLayoutManager(StockDetailActivity.this));
        rv.setNestedScrollingEnabled(false);
        ArrayList<NewsItem> newsItems = new ArrayList<>();
        RequestQueue rq = Volley.newRequestQueue(StockDetailActivity.this);
        String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/news/" + ticker;

        JsonObjectRequest newsRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject response) {
            try {
                JSONArray articles = response.getJSONArray("articles");
                for(int i = 0; i < articles.length(); i++){

                        JSONObject article = articles.getJSONObject(i);
                        String source = article.getJSONObject("source").getString("name");
                        String title = article.getString("title");
                        String articleUrl = article.getString("url");
                        String imgUrl = article.getString("urlToImage");
                        String pubDate = article.getString("publishedAt");
                        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                        Instant iparse = Instant.parse(pubDate);
//                        Date published = sdf.parse(pubDate);
                        LocalDate published = LocalDate.parse(pubDate, DateTimeFormatter.ISO_DATE_TIME);
                        LocalDate now = LocalDate.now();
                       long dayDiff = ChronoUnit.DAYS.between(published,now);
                       String dateFrom = "";
                       if(dayDiff <=1){
                            dateFrom = dayDiff + " day ago";
                       }
                       else{
                            dateFrom = dayDiff + " days ago";
                       }
//                        String dateFrom = "something";
                        NewsItem newsItem = new NewsItem(imgUrl,source, dateFrom,title,articleUrl);
                        newsItems.add(newsItem);
                }
                NewsAdapter adapter = new NewsAdapter(StockDetailActivity.this, newsItems);
                rv.setAdapter(adapter);
            } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        rq.add(newsRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getHistoricalData(String ticker){
        RequestQueue rq = Volley.newRequestQueue(StockDetailActivity.this);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/history/" + ticker+ '/' + df.format(date);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(tag, response);
                wv.setWebViewClient(new WebViewClient(){
                    public void onPageFinished(WebView view,String url){
                        wv.loadUrl("javascript:loadGraph(" + response + ")");
                    }

                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        rq.add(stringRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (rq != null) {
            rq.cancelAll(reqTag2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}