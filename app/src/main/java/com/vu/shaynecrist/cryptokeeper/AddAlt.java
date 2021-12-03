package com.vu.shaynecrist.cryptokeeper;

//        To get root access in Device Monitor, open cmd prompt at shayn/appdata/local/android/sdk/platform-tools
//        and run the following commands:
//        $ > adb shell
//        generic_x86:/ $
//        generic_x86:/ $ exit
//        $ > adb root
//        restarting adbd as root
//        $ > adb shell
//        generic_x86:/ #


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAlt extends AppCompatActivity {

    // Erase after testing
    private Button viewInfo;
    private Button delete;

    // Create class variables
    private Button finished;
    private EditText symbolText;
    private EditText dateText;
    private EditText amountText;
    private EditText rateText;

    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 999;

    private static final String ENDPOINT = "https://bittrex.com/api/v1.1/public/getcurrencies";
    private RequestQueue requestQueue;
    private String jsonResult;

    // Variables and objects for DB
    DatabaseHelper mDatabaseHelper;
    String id;
    String sym;
    String date;
    String amount;
    String rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alt);

        // Pull requests for JSON from Bittrex
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Call to request alt coin info
        fetchAlts();

        setCurrentDateOnView();
        addListenerOnEditText();

        mDatabaseHelper = new DatabaseHelper(this);
        symbolText = (EditText) findViewById(R.id.symText);
        amountText = (EditText) findViewById(R.id.amountText);
        rateText = (EditText) findViewById(R.id.rateText);

        // Erase after testing
        viewInfo = (Button) findViewById(R.id.button2);
        viewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDatabaseHelper.numberOfRows() > 0) {
                    Cursor resultSet;
                    String result;
                    resultSet = mDatabaseHelper.getData();
                    resultSet.moveToPosition(1);
                    result = resultSet.getString(1);
//                    Log.i("Database Pull Result", result);
//                    String sym = symbolText.getText().toString();
//                    sym = sym.toUpperCase();
//                    Cursor resultSet;
//                    String result;
//                    resultSet = mDatabaseHelper.getData(sym); // Starts at 1, coin entries
//                    resultSet.moveToFirst();
//                    result = resultSet.getString(3); // 0 is ID, 1 is name, 2 is date purchased, 3 is amount purchased, 4 is purchase rate
//                    toastMessage(result);
                } else {
                    toastMessage("There are no entries");
                }
            }
        });
        delete = (Button) findViewById(R.id.buttonDelete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseHelper.deleteAll();
            }
        });

        // Listener for 'Finished' button click
        finished = (Button) findViewById(R.id.finishedAddingAlt);
        finished.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sym = symbolText.getText().toString().toUpperCase();
                date = dateText.getText().toString();
                amount = amountText.getText().toString();
                rate = rateText.getText().toString();
                if(!TextUtils.isEmpty(sym) && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(amount) && !TextUtils.isEmpty(rate)) { // Ensures no fields are blank
                    if(sym.length() >= 3) { // Ensures symbol is at least 3 characters and doesn't already exist in the database
                        ArrayList<String> symbolsAlready = new ArrayList<>(mDatabaseHelper.returnSymbols());
                        if(!symbolsAlready.contains(sym)) { // Checks the Bittrex exchange to make sure the altcoin is tradeable on the exchange
//                            Log.i("JSON Results", jsonResult);
                            if(jsonResult.matches(".*\\b" + sym + "\\b.*")) {
                                AddData(sym, date, amount, rate);
                                finish();
                            } else {
                                toastMessage(sym + " not listed on the exchange!");
                            }
                        } else{
                            toastMessage(sym + " already added!");
                        }
                    } else {
                    toastMessage("Please enter a valid Altcoin symbol.");
                    }
                } else {
                    toastMessage("All entries must be filled!");
                }
            }
        });
    }

    public void AddData(String sym, String date, String amount, String rate) {
        boolean insertData = mDatabaseHelper.addData(sym, date, amount, rate);
        if(insertData) {
//            toastMessage("Data successfully inserted");
        } else {
            toastMessage("Something went wrong");
        }
    }

    // Displays the current date
    public void setCurrentDateOnView() {
        dateText = (EditText) findViewById(R.id.dateText);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // Set current date into EditText
        dateText.setText(new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" "));
    }

    // Create handler for the EditText field for when clicked
    public void addListenerOnEditText(){
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    // Ran when date picker is shown
    @Override
    protected Dialog onCreateDialog(int id){
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month, day);
        }
        return null;
    }

    // Dialog box for the date picker
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        // When dialog box is closed, below method will be called
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay){
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // Set selected date into edittext
            dateText.setText(new StringBuilder().append(month + 1).append("-").append(day).append("-").append(year).append(" "));
        }
    };

    // Customizable toast method
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Methods for retrieving the JSON data from Bittrex
    private void fetchAlts() {
        StringRequest request = new StringRequest(Request.Method.GET, ENDPOINT, onAltsLoaded, onAltsError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onAltsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
//            Log.i("Response", response);
            jsonResult = response;
        }
    };

    private final Response.ErrorListener onAltsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("ERROR", error.toString());
        }
    };
}
