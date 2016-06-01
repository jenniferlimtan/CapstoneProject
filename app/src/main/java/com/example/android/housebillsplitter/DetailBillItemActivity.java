package com.example.android.housebillsplitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailBillItemActivity extends AppCompatActivity {

    private static final String DETAILBILLITEMFRAGMENT_TAG = "DBIFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bill_item);

        if (savedInstanceState == null) {

            DetailBillItemFragment frag = new DetailBillItemFragment();
            Bundle args = new Bundle();
            args.putString(DetailBillItemFragment.KEY_BILLITEM_ID, (getIntent() == null ? null : getIntent().getStringExtra(DetailBillItemFragment.KEY_BILLITEM_ID)));
            frag.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_bill_item_container, frag, DETAILBILLITEMFRAGMENT_TAG)
                    .commit();
        }
    }
}
