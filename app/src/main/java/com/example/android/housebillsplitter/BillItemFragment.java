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

public class BillItemFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener{

    private final int LOADER_ID = 20;

    private static final String[] FROM_IDS = {
            HouseBillSplitterContract.BillItemEntry.COLUMN_DESC,
            HouseBillSplitterContract.BillItemEntry.COLUMN_AMT,
            HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE
    };

    private final static int[] TO_IDS = {
            R.id.billitem_desc,
            R.id.billitem_amt,
            R.id.billitem_type
    };

    private static final String[] PROJECTION = {
            HouseBillSplitterContract.BillItemEntry.COLUMN_ID,
            HouseBillSplitterContract.BillItemEntry.COLUMN_DESC,
            HouseBillSplitterContract.BillItemEntry.COLUMN_AMT,
            HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE
    };

    private ListView mBillItemList;
    private TextView v_total_amount;

    private DecimalFormat df;

    private SimpleCursorAdapter mCursorAdapter;
    private int selected;
    private static final int SELECTED_ID_INDEX = 0;

    public BillItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bill_item, container, false);
        v_total_amount = (TextView) rootView.findViewById(R.id.total_amt);
        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        df = new DecimalFormat(getString(R.string.amount_format));

        mBillItemList =  (ListView) getActivity().findViewById(R.id.billitem_list);
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.billitem_list_item,
                null,
                FROM_IDS, TO_IDS,
                0) {
            @Override
            public void setViewText(TextView v, String text) {
                if (v.getId() == R.id.billitem_amt) {
                    text = df.format(Double.parseDouble(text.isEmpty() ? "0" : text));
                }
                super.setViewText(v, text);
            }
        };
        mBillItemList.setAdapter(mCursorAdapter);
        mBillItemList.setOnItemClickListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                HouseBillSplitterContract.BillItemEntry.CONTENT_URI,
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
        Intent detailIntent = new Intent(getActivity(), DetailBillItemActivity.class);
        detailIntent.putExtra(DetailBillItemFragment.KEY_BILLITEM_ID, String.valueOf(id));
        startActivity(detailIntent);
    }

    private void loadTotalAmt(Cursor data){
        Double total_amount=0.0;
        while(data.moveToNext()) {
            total_amount = total_amount + data.getDouble(2);
        }
        v_total_amount.setText(df.format(total_amount));
    }

}
