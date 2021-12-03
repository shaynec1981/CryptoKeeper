package com.vu.shaynecrist.cryptokeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CKSettings extends AppCompatActivity {

    // Create class variables
    private Button finished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cksettings);

        // Listener for 'Finished' button click
        finished = (Button) findViewById(R.id.finishedSettings);
        finished.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
    }
}
