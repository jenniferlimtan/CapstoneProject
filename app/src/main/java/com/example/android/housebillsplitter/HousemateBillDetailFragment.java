package com.example.android.housebillsplitter;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.housebillsplitter.data.HouseBillSplitterContract;

import java.text.DecimalFormat;

public class HousemateBillDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String KEY_HOUSEMATE_ID = "housemate_id";
    public static final String KEY_TOTAL_AMT = "total_amount";

    private final int LOADER_ID = 60;

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

    private ListView mBillItemList;

    private SimpleCursorAdapter mCursorAdapter;
    private String housemate_id;
    private String total_amount;

    private int selected;
    private static final int SELECTED_ID_INDEX = 0;

    private DecimalFormat df ;

    private static final String[] PROJECTION = {
            HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +"." + HouseBillSplitterContract.BillSharingEntry.COLUMN_ID + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_ID,
            HouseBillSplitterContract.BillItemEntry.COLUMN_DESC,
            HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +"." + HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT,
            HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE +"|| ' (' || "+ HouseBillSplitterContract.BillSharingEntry.COLUMN_PERCENTAGE +" || '%)' AS " +   HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE
    };

    public HousemateBillDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            housemate_id = arguments.getString(KEY_HOUSEMATE_ID);
            total_amount = arguments.getString(KEY_TOTAL_AMT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_housemate_bill_detail, container, false);
        TextView v_total_amount = (TextView) rootView.findViewById(R.id.total_amt);
        if(v_total_amount != null)
            v_total_amount.setText(total_amount);
        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        df = new DecimalFormat(getString(R.string.amount_format));
        mBillItemList =  (ListView) getActivity().findViewById(R.id.housemate_bill_detail_list);
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.billitem_list_item,
                null,
                FROM_IDS, TO_IDS,
                0){
            @Override
            public void setViewText(TextView v, String text) {
                if(v.getId() == R.id.billitem_amt) {
                    text = df.format(Double.parseDouble(text.isEmpty()? "0":text));
                }
                else if(v.getId() == R.id.billitem_type) {
                    if(text.startsWith(getString(R.string.split_equally)))
                        text = getString(R.string.split_equally);
                }
                super.setViewText(v, text);
            }

        };
        mBillItemList.setAdapter(mCursorAdapter);
        mBillItemList.setEnabled(false);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                HouseBillSplitterContract.BillSharingEntry.buildBillSharingByHousemateIDUri(housemate_id),
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }


}
