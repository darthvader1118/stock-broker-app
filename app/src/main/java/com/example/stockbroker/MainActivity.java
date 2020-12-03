package com.example.stockbroker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
//import com.example.stockbroker.
//import android.widget.SearchView;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {
    String tag = "activityOne";
    TextView textView;
    TextView tiingo, TodayDate;
    RecyclerView rc,favoritesView;
    ProgressBar spinner;
    SectionedRecyclerViewAdapter sectionAdapter;
    private AutoSuggestAdapter autoSuggestAdapter;
    ArrayList<String> favoritesList;
    ArrayList<Portfolio> portfolioList;
    public static final String reqTag = "Main request";
    RequestQueue rq;
    ConstraintLayout constraintLayout;
    Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_StockBroker);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(tag,"--onCreate--");
        constraintLayout = (ConstraintLayout) findViewById(R.id.main_page);
        constraintLayout.setVisibility(View.GONE);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        rq = Volley.newRequestQueue(MainActivity.this);
//        textView = (TextView) findViewById(R.id.textView);
        tiingo = (TextView) findViewById(R.id.tiingo);
        tiingo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.tiingo.com/"));
                startActivity(i);
            }
        });
        TodayDate = (TextView) findViewById(R.id.today_date);
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        TodayDate.setText(df.format(today));
        rc = (RecyclerView) findViewById(R.id.StockList);
        rc.setNestedScrollingEnabled(false);
//        rc.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        PortfolioAdapter pa = new PortfolioAdapter(this, getPortfolioData());
//        rc.setAdapter(pa);
//        favoritesView = (RecyclerView) findViewById(R.id.FavoritesList);
//        favoritesView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        FavoritesAdapter fa = new FavoritesAdapter(this, getFavoritesData());
//        favoritesView.setAdapter(fa);

        sectionAdapter = new SectionedRecyclerViewAdapter();
        rc.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        portfolioList = getPortfolioData();
        PortfolioSection portfolioSection = new PortfolioSection(portfolioList,this, rq);
        favoritesList = getFavoritesData();

        sectionAdapter.addSection(portfolioSection);
        sectionAdapter.addSection(new FavoritesSection(favoritesList,this, rq));
        enableSwipeToDeleteAndUndo();



        rc.setAdapter(sectionAdapter);
        enableDragandDrop();

//        spinner.setVisibility(View.GONE);



                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateNetWorth();
                            }
                        });

                    }
                },0,15000);






    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(tag,"--onStart--");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(tag,"--onResume--");
        timer = new Timer();
        sectionAdapter = new SectionedRecyclerViewAdapter();
        rc.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        PortfolioSection portfolioSection = new PortfolioSection(getPortfolioData(),this, rq);
        favoritesList = getFavoritesData();
        portfolioList = getPortfolioData();
        sectionAdapter.addSection(portfolioSection);
        sectionAdapter.addSection(new FavoritesSection(favoritesList,this, rq));

        rc.setAdapter(sectionAdapter);
        enableSwipeToDeleteAndUndo();
        enableDragandDrop();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populateNetWorth();
                    }
                });

            }
        },0,15000);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(tag,"--onRestart--");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(tag,"--onPause--");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(tag,"--onStop--");
        //timer.cancel();
        if(rq!=null){
            rq.cancelAll("item");
        }
        for(int i = 0; i < rc.getChildCount(); i++){
            PortfolioHolder item = (PortfolioHolder) rc.findViewHolderForAdapterPosition(i);
            String itemString = item.toString();

            item.destroyTimer();
        }
        timer.cancel();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(tag,"--onDestroy--");
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
       SearchView sv =  (SearchView) menuItem.getActionView();
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) sv.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(R.color.white);
        String dataArr[] = {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};
        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);

        searchAutoComplete.setAdapter(newsAdapter);
        searchAutoComplete.setThreshold(3);


        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                String ticker = queryString.split("-")[0];
                searchAutoComplete.setText("" + ticker);
                Intent detailIntent = new Intent(MainActivity.this, StockDetailActivity.class);
                detailIntent.putExtra("ticker", ticker);
                MainActivity.this.startActivity(detailIntent);
//                Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
            }
        });

//        autoSuggestAdapter = new AutoSuggestAdapter(this,
//                android.R.layout.simple_dropdown_item_1line);
//        searchAutoComplete.setAdapter(autoSuggestAdapter);
//        searchAutoComplete.setThreshold(3);
        sv.setQueryHint("search for Stock");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(tag, query);
                Intent detailIntent = new Intent(MainActivity.this, StockDetailActivity.class);
                detailIntent.putExtra("ticker", query);
                MainActivity.this.startActivity(detailIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               makeAutoCompleteRequest(newText, searchAutoComplete);
                Log.i(tag, newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public void makeAutoCompleteRequest(String searchText, SearchView.SearchAutoComplete searchAutoComplete){


        String url = "http://stockbroker2-env.eba-3yim8bsf.us-west-2.elasticbeanstalk.com/search/" + searchText;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            ArrayList<String> autoComplete = new ArrayList<String>();
                            JSONArray resArray = new JSONArray(response);
                            for(int i = 0; i<resArray.length(); i++){
                                JSONObject item = resArray.getJSONObject(i);
                                String name = item.getString("name");
                                String ticker = item.getString("ticker");
                                autoComplete.add(ticker + '-' + name);
                            }
                            ArrayAdapter<String> autoAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_dropdown_item_1line, autoComplete);
                            searchAutoComplete.setAdapter(autoAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i(tag,response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.i(tag, error.toString());
            }
        });
        stringRequest.setTag(reqTag);
        rq.add(stringRequest);
    }

    public ArrayList<Portfolio> getPortfolioData(){
        ArrayList<Portfolio> portfolioItems = new ArrayList<>();
        SharedPreferences portfolio = getSharedPreferences("portfolio", MODE_PRIVATE);
        Gson gson = new Gson();
        Map<String,?> allEntries = portfolio.getAll();
        for(Map.Entry<String,?> entry: allEntries.entrySet()){
            if(!entry.getKey().equals("cash")){
                Portfolio item = gson.fromJson((String) entry.getValue(),Portfolio.class);
                portfolioItems.add(item);
            }
        }
        return portfolioItems;
    }

    public ArrayList<String> getFavoritesData(){
        ArrayList<String> favoritesItems = new ArrayList<>();
        SharedPreferences watchlist = getSharedPreferences("watchlist", MODE_PRIVATE);
        Map<String,?> allEntries = watchlist.getAll();
        for(Map.Entry<String,?> entry: allEntries.entrySet()){
            favoritesItems.add((String) entry.getValue());
        }
        spinner.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);
        return  favoritesItems;
    }
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if(rq!=null){
                    rq.cancelAll("item");
                }

                int pos = viewHolder.getAdapterPosition();
               Section portfolioSection =  sectionAdapter.getSectionForPosition(viewHolder.getAdapterPosition());
                if(portfolioSection instanceof PortfolioSection){
                    Toast.makeText(MainActivity.this,"can't delete portfolio item",Toast.LENGTH_SHORT).show();
                    sectionAdapter.notifyDataSetChanged();

                }
                else {

                    final int position = sectionAdapter.getPositionInSection(viewHolder.getAdapterPosition());
                    final String item = getFavoritesData().get(position);
                    int count = sectionAdapter.getItemCount();
                    SharedPreferences watchlist = getSharedPreferences("watchlist", MODE_PRIVATE);
                    SharedPreferences.Editor editor = watchlist.edit();
                    FavoritesSection favoritesSection = (FavoritesSection) sectionAdapter.getSectionForPosition(pos);


                    //sectionAdapter.notifyItemRemoved(i);
                    //favoritesList.remove(position);
                   // favoritesSection.setFavoritesItems(favoritesList);
                    PortfolioHolder swipedItemHolder = (PortfolioHolder) viewHolder;
                    swipedItemHolder.destroyTimer();
                    favoritesSection.removeItem(position);
//                    timer.cancel();
//                    timer = new Timer();
                    sectionAdapter.notifyItemRemoved(pos);
//                    sectionAdapter = new SectionedRecyclerViewAdapter();
//                    PortfolioSection portfolioSectionNew = new PortfolioSection(getPortfolioData(),MainActivity.this);
//                    sectionAdapter.addSection(portfolioSectionNew);
//                    sectionAdapter.addSection(new FavoritesSection(favoritesList, MainActivity.this));
//                    sectionAdapter.notifyItemRangeChanged(pos,favoritesList.size());
//                    sectionAdapter.removeSection(favoritesSection);
//                    sectionAdapter.addSection(new FavoritesSection(favoritesList, MainActivity.this));
//                    rc.setAdapter(sectionAdapter);
//                    sectionAdapter.notifyDataSetChanged();

//                    rc.setAdapter(sectionAdapter);
                    editor.remove(item.toLowerCase());
                    editor.commit();
                }



            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rc);
    }
    private void enableDragandDrop(){

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.START|ItemTouchHelper.END,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                PortfolioHolder from = (PortfolioHolder) viewHolder;
                PortfolioHolder to = (PortfolioHolder) target;
                from.destroyTimer();
                to.destroyTimer();
                SectionedRecyclerViewAdapter sectionedRecyclerViewAdapter = (SectionedRecyclerViewAdapter) recyclerView.getAdapter();
                if(rq!=null){
                    rq.cancelAll("item");
                }
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Section fromSection = sectionedRecyclerViewAdapter.getSectionForPosition(fromPosition);
                Section toSection = sectionedRecyclerViewAdapter.getSectionForPosition(toPosition);
                boolean sameSection = fromSection.getClass().equals(toSection.getClass());
                if(sameSection){
                    if(fromSection instanceof FavoritesSection){
                        Collections.swap(favoritesList,sectionedRecyclerViewAdapter.getPositionInSection(fromPosition),sectionedRecyclerViewAdapter.getPositionInSection(toPosition));

                    }
                    else{
                        Collections.swap(portfolioList,sectionedRecyclerViewAdapter.getPositionInSection(fromPosition),sectionedRecyclerViewAdapter.getPositionInSection(toPosition));
                        PortfolioSection newPortfolio = (PortfolioSection)sectionedRecyclerViewAdapter.getSection(fromPosition);
                        newPortfolio.portfolioItems= portfolioList;
                        sectionedRecyclerViewAdapter.notifyItemMoved(fromPosition,toPosition);

                    }
                }
                else{
                        Toast.makeText(MainActivity.this,"Can't move item there", Toast.LENGTH_SHORT).show();
                }

                sectionedRecyclerViewAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(simpleCallback);
        itemTouchhelper.attachToRecyclerView(rc);
    }

    public void populateNetWorth(){
        Double stockValue = 0.0;
        for(int i = 0 ; i < rc.getChildCount(); i++){
            PortfolioHolder portfolioHolderItem = (PortfolioHolder) rc.findViewHolderForAdapterPosition(i);
            if(portfolioHolderItem.getSharesView().matches(".*\\d.*") && portfolioHolderItem.getSharesView().contains("shares")){
                String shareText = portfolioHolderItem.getSharesView();
                Double shares = Double.parseDouble(shareText.split(" ")[0]);
                Double price = Double.parseDouble(portfolioHolderItem.getPrice().toString());
                stockValue += shares*price;
            }
        }
        SharedPreferences portfolio = getSharedPreferences("portfolio", MODE_PRIVATE);
        DecimalFormat df2 = new DecimalFormat("#.##");

        float cash = portfolio.getFloat("cash", 20000);
        Double total = stockValue + cash;
        TextView netWorth = (TextView) findViewById(R.id.net_worth_data);
        netWorth.setText(df2.format(total));

    }

}