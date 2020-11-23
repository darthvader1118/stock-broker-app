package com.example.stockbroker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
//import com.example.stockbroker.
//import android.widget.SearchView;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String tag = "activityOne";
    TextView textView;
    RecyclerView rc;
    private AutoSuggestAdapter autoSuggestAdapter;
//    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(tag,"--onCreate--");
//        textView = (TextView) findViewById(R.id.textView);
        rc = (RecyclerView) findViewById(R.id.StockList);


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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(tag,"--onDestroy--");
    }

    public void onClickText(View v){
        Intent myIntent = new Intent(MainActivity.this, newTestActivity.class);
//        Date date = new Date();
//        textView.setText("Today is:" + date.toString());
//        Toast.makeText(this, "button clicked", Toast.LENGTH_LONG).show();
        MainActivity.this.startActivity(myIntent);


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

        RequestQueue rq = Volley.newRequestQueue(MainActivity.this);
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
                    Log.i(tag, "that didn't work");
            }
        });
        rq.add(stringRequest);
    }
}