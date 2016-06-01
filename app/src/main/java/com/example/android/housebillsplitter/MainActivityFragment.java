package com.example.android.housebillsplitter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.housebillsplitter.data.HouseBillSplitterContract;

import java.text.DecimalFormat;

public class MainActivityFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener{

    private final int LOADER_ID = 80;

    private static final String[] FROM_IDS = {
            HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_NAME,
            HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT
    };

    private final static int[] TO_IDS = {
            R.id.housemate_name,
            R.id.billsharing_amt
    };
    private static final String[] PROJECTION = {
            HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_ID + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_ID,
            HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_NAME + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_NAME,
            "SUM("+ HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +"." + HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT + ") as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT
    };

    private SimpleCursorAdapter mCursorAdapter;

    private ListView mBillingList;
    private TextView v_total_amount;

    private DecimalFormat df;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        v_total_amount = (TextView) rootView.findViewById(R.id.total_amt);
        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {

        df = new DecimalFormat(getString(R.string.amount_format));
        mBillingList =  (ListView) getActivity().findViewById(R.id.billing_list);
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.billing_list_item,
                null,
                FROM_IDS, TO_IDS,
                0){
            @Override
            public void setViewText(TextView v, String text) {
                if(v.getId() == R.id.billsharing_amt) {
                    text = df.format(Double.parseDouble(text.isEmpty()? "0":text));
                }
                super.setViewText(v, text);
            }

        };
        mBillingList.setAdapter(mCursorAdapter);
        mBillingList.setOnItemClickListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                HouseBillSplitterContract.BillSharingEntry.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadTotalAmt(data);
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        TextView housemate_tv = (TextView) view.findViewById(R.id.housemate_name);
        TextView total_amt_tv = (TextView) view.findViewById(R.id.billsharing_amt);

        Intent detailIntent = new Intent(getActivity(), HousemateBillDetailActivity.class);
        detailIntent.putExtra(HousemateBillDetailFragment.KEY_HOUSEMATE_ID, String.valueOf(id));
        detailIntent.putExtra(HousemateBillDetailActivity.KEY_HOUSEMATE_NAME, housemate_tv.getText().toString());
        detailIntent.putExtra(HousemateBillDetailFragment.KEY_TOTAL_AMT, total_amt_tv.getText().toString());
        startActivity(detailIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            getActivity().overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.slide_out_right);
        }
    }

    private void loadTotalAmt(Cursor data){
        Double total_amount=0.0;
       while(data.moveToNext()) {
           total_amount = total_amount + data.getDouble(2);
       }
        v_total_amount.setText(df.format(total_amount));
    }

}
