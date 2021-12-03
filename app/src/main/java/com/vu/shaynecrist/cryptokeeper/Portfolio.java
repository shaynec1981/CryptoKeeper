package com.vu.shaynecrist.cryptokeeper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Portfolio extends AppCompatActivity {

    // Create class variables
    NestedScrollView parentView;
    private String exchangeResult = "0.00000000";
    private static final String ENDPOINT = "https://bittrex.com/api/v1.1/public/getticker?market=btc-";
    private RequestQueue queue;

    ArrayList<String> coinLastList;

    private TextView inf1, inf2, inf3;
    private TableLayout tl;
    private TableRow tr;

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coinLastList = new ArrayList<>();

        queue = Volley.newRequestQueue(this);
        // Get our top level view container
        parentView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        // Get our database
        mDatabaseHelper = new DatabaseHelper(this);

        // Create local variables
        String appVersion = getResources().getString(R.string.app_version); // Create a string variable containing the value of the app_version string in strings.xml
        String appName = getResources().getString(R.string.app_name); // Create a string variable containing the value of the app_name string in strings.xml
        final ImageView splashBack = (ImageView) findViewById(R.id.splashBack);
        final TextView splashText = (TextView) findViewById(R.id.splashText);

        // Create an object containing the toolbar in order to customize it
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(appName + " " + appVersion);

        // Pause for 3000 ms then hide splash screen and text
        new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // not needed
            }

            @Override
            public void onFinish(){
                splashBack.setVisibility(View.GONE);
                splashText.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    // Clear and recreate the table views every time main activity is brought back into focus
    @Override
    protected void onResume() {
        super.onResume();
        coinLastList.clear();
        int numOfRows = mDatabaseHelper.numberOfRows();
        Log.i("TEST TEST TEST", "Number of rows: " + Integer.toString(numOfRows));
        Cursor tableData;
        String sym;
        tableData = mDatabaseHelper.getData();
        int i = 0;
        while(i < numOfRows) {
            Log.i("TEST TEST TEST", Integer.toString(i));
            tableData.moveToPosition(i);
            sym = tableData.getString(1);
            Log.i("TEST TEST TEST", sym);
            fetchAlts(sym);
            i++;
        }
        Log.i("TEST TEST TEST", coinLastList.toString());
        clearTable();
        createTable();
    }

    private void createTable() {
        int numOfRows = mDatabaseHelper.numberOfRows();
        for(int i = 0; i < numOfRows; i++) {
            Cursor tableData;
            String sym;
            tableData = mDatabaseHelper.getData();
            tableData.moveToPosition(i);
            sym = tableData.getString(1);
//            fetchAlts(sym);
            tl = (TableLayout) findViewById(R.id.contentTableLayout);
            tr = new TableRow(this);
            if((i % 2) == 0) {
                tr.setBackgroundColor(Color.GRAY);
            }
            // Create 3 colums (column id, stretchable)
            tl.setColumnStretchable(0, true);
            tl.setColumnStretchable(1, true);
            tl.setColumnStretchable(2, true);
            // Add textViews to each column
            inf1 = new TextView(this);
            inf2 = new TextView(this);
            inf3 = new TextView(this);
            inf1.setText(sym);
            inf1.setTextSize(18);
            inf1.setGravity(Gravity.CENTER);
            inf1.setHeight(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics()))); // Convert 48dp to pixels depending on display resolution
            Log.i("TEST TEST TEST", "SHOULD SEE ME DEAD LAST!!!!!!!!!!");
            if(coinLastList.size() != 0) {
                inf2.setText(coinLastList.get(i));
            }
            inf2.setTextSize(18);
            inf2.setGravity(Gravity.CENTER);
            inf2.setHeight(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics())));
            inf3.setText("1..2..3..");
            inf3.setTextSize(18);
            inf3.setGravity(Gravity.CENTER);
            inf3.setHeight(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics())));
            tr.addView(inf1);
            tr.addView(inf2);
            tr.addView(inf3);
            tl.addView(tr);
        }
    }

    // Completely erases all rows in the table
    private void clearTable() {
        if(tl != null) {
            tl.removeAllViews();
        }
    }

    // Begin JSON request methods
    private void fetchAlts(String symbol) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ENDPOINT + symbol, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("TEST TEST TEST", "I GOT HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        exchangeResult = response.substring(response.lastIndexOf(":") + 1, response.lastIndexOf(":") + 11); // Parse the JSON data to find the current rate of the given altcoin
                        Log.i("TEST TEST TEST", "Exchange Result = " + exchangeResult);
                        coinLastList.add(exchangeResult);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Something went wrong!\n" + error);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    // End JSON request methods


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portfolio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_new:
                launchActivity(AddAlt.class);
                return true;
            case R.id.action_settings:
                launchActivity(CKSettings.class);
                return true;
            case R.id.action_about:
                launchActivity(CKAbout.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchActivity(Class activity){
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    // Customizable toast method
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
