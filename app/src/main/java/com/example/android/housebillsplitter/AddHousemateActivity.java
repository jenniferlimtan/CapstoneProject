package com.example.android.housebillsplitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class AddHousemateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_housemate);
        getSupportActionBar().setTitle(getString(R.string.add_housemate));
    }

}
