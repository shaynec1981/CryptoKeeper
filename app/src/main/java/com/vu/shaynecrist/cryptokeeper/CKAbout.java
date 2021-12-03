package com.vu.shaynecrist.cryptokeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class CKAbout extends AppCompatActivity {

    // Create class variables
    private Button finished;
    private Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ckabout);

        // Listener for 'Finished' button click
        finished = (Button) findViewById(R.id.finishedAbout);
        finished.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        test = (Button) findViewById(R.id.testButton);
        test.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Do something when button is clicked
            }
        });
    }

}
