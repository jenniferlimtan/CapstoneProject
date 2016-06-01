package com.example.android.housebillsplitter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EditBillItemActivity extends AppCompatActivity {

    private static final String EDITBILLITEMFRAGMENT_TAG = "EBIFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bill_item);
        String bill_item_id =  (getIntent() != null ? getIntent().getStringExtra(DetailBillItemFragment.KEY_BILLITEM_ID) : null);

        if(savedInstanceState == null) {
            EditBillItemFragment frag = new EditBillItemFragment();
            Bundle args = new Bundle();
            args.putString(DetailBillItemFragment.KEY_BILLITEM_ID, bill_item_id);
            frag.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.edit_bill_item_container, frag, EDITBILLITEMFRAGMENT_TAG)
                    .commit();
        }

    }


}
