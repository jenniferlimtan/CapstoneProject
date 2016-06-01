package com.example.android.housebillsplitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BillItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_item);
        getSupportActionBar().setTitle(getString(R.string.title_billitems));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.billitem_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BillItemActivity.this, EditBillItemActivity.class));
            }
        });
    }
}
