package com.example.android.housebillsplitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HousemateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housemate);
        getSupportActionBar().setTitle(getString(R.string.title_housemate));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_housemate_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.add_housemate), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.add_housemate), null).show();
                startActivity(new Intent(HousemateActivity.this, AddHousemateActivity.class));
            }
        });
    }

}
