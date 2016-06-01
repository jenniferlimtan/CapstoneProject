package com.example.android.housebillsplitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class HousemateBillDetailActivity extends AppCompatActivity {

    public static final String KEY_HOUSEMATE_NAME = "housemate_name";

    private static final String HOUSEMATE_BILL_DETAILFRAGMENT_TAG = "HBDFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_housemate_bill_detail);

        if (savedInstanceState == null) {

            HousemateBillDetailFragment frag = new HousemateBillDetailFragment();
            Bundle args = new Bundle();
            if(getIntent() == null){
                args.putString(HousemateBillDetailFragment.KEY_HOUSEMATE_ID, null);
            }else {
                args.putString(HousemateBillDetailFragment.KEY_HOUSEMATE_ID, getIntent().getStringExtra(HousemateBillDetailFragment.KEY_HOUSEMATE_ID));
                args.putString(HousemateBillDetailFragment.KEY_TOTAL_AMT, getIntent().getStringExtra(HousemateBillDetailFragment.KEY_TOTAL_AMT));
                getSupportActionBar().setTitle(getIntent().getStringExtra(KEY_HOUSEMATE_NAME));
            }
            frag.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.housemate_bill_detail_container, frag, HOUSEMATE_BILL_DETAILFRAGMENT_TAG)
                    .commit();
        }
    }
}
