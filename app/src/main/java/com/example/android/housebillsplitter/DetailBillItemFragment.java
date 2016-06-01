package com.example.android.housebillsplitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.housebillsplitter.data.HouseBillSplitterContract;
import com.example.android.housebillsplitter.data.HouseBillSplitterProvider;

import java.text.DecimalFormat;


public class DetailBillItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String KEY_BILLITEM_ID = "bill_item_id";

    private static final int DETAIL_LOADER = 30;
    private static final int BILLSHARING_LOADER = 40;

    private static final String[] DETAIL_COLUMNS = {
            HouseBillSplitterContract.BillItemEntry.COLUMN_ID,
            HouseBillSplitterContract.BillItemEntry.COLUMN_DESC,
            HouseBillSplitterContract.BillItemEntry.COLUMN_AMT,
            HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE
    };

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_DESC = 1;
    private static final int COLUMN_AMT = 2;
    private static final int COLUMN_TYPE = 3;

    private static final String[] FROM_IDS = {
            HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_NAME,
            HouseBillSplitterContract.BillSharingEntry.COLUMN_PERCENTAGE,
            HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT
    };

    private final static int[] TO_IDS = {
            R.id.housemate_name,
            R.id.percentage,
            R.id.billsharing_amt
    };

    private static final String[] PROJECTION = {
            HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +"."+ HouseBillSplitterContract.BillSharingEntry.COLUMN_ID ,
            HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_ID + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID,
            HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_NAME + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_NAME,
            HouseBillSplitterContract.BillSharingEntry.COLUMN_PERCENTAGE,
            HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT
    };

    private ListView mBillSharingList;
    private TextView descText;
    private TextView amtText;
    private TextView typeText;

    private DecimalFormat df;

    private SimpleCursorAdapter mCursorAdapter;
    private String bill_item_id;

    public DetailBillItemFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            bill_item_id = arguments.getString(KEY_BILLITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_bill_item, container, false);

        descText = (TextView) rootView.findViewById(R.id.billitem_desc);
        amtText = (TextView) rootView.findViewById(R.id.billitem_amt);
        typeText = (TextView) rootView.findViewById(R.id.billitem_type);

        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        df = new DecimalFormat(getString(R.string.amount_format));
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        mBillSharingList =  (ListView) getActivity().findViewById(R.id.billsharing_list);
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.billsharing_list_item,
                null,
                FROM_IDS, TO_IDS,
                0){
            @Override
            public void setViewText(TextView v, String text) {
                if(v.getId() == R.id.billsharing_amt) {
                    text = df.format(Double.parseDouble(text.isEmpty()? "0":text));
                }
                else if (v.getId() == R.id.percentage){
                    if(typeText.getText().equals(getString(R.string.split_equally))){
                        text = getString(R.string.constant_dash);
                    }
                    else{
                        text = (text.isEmpty()? getString(R.string.constant_dash): text + getString(R.string.constant_percent));
                    }
                }
                super.setViewText(v, text);
            }
        };
        mBillSharingList.setAdapter(mCursorAdapter);
        mBillSharingList.setEnabled(false);
        getLoaderManager().initLoader(BILLSHARING_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_billitem, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if  (id == R.id.action_delete) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.permission_delete_billitem)
                    .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(delete()){
                                Toast.makeText(getContext(), getString(R.string.msg_deleted), Toast.LENGTH_LONG).show();
                                NavUtils.navigateUpFromSameTask(getActivity());
                            }else{
                                Toast.makeText(getContext(), getString(R.string.msg_delete_failed), Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.action_cancel, null)
                    .show();
        }
        else if (id == R.id.action_edit)  {
            Intent editIntent = new Intent(getActivity(), EditBillItemActivity.class);
            editIntent.putExtra(DetailBillItemFragment.KEY_BILLITEM_ID, String.valueOf(bill_item_id));
            startActivity(editIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean delete(){
        int billsharing_rowsDeleted  = getContext().getContentResolver().delete(HouseBillSplitterContract.BillSharingEntry.CONTENT_URI,
                HouseBillSplitterContract.BillSharingEntry.COLUMN_BILLITEM_ID +" = ?",
                new String[]{bill_item_id});

        int rowsDeleted = getContext().getContentResolver().delete(HouseBillSplitterContract.BillItemEntry.CONTENT_URI,
                    HouseBillSplitterContract.BillItemEntry.COLUMN_ID + " = ?",
                    new String[]{bill_item_id});

        HouseBillSplitterProvider.updateWidgets(getContext());
        return rowsDeleted>0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != bill_item_id) {
            if(id == DETAIL_LOADER) {
                return new CursorLoader(
                        getActivity(),
                        HouseBillSplitterContract.BillItemEntry.buildBillItemUri(Long.parseLong(bill_item_id)),
                        DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
            }
            else if (id == BILLSHARING_LOADER){
                return new CursorLoader(
                        getActivity(),
                        HouseBillSplitterContract.BillSharingEntry.buildBillSharingByBillItemIDUri(bill_item_id),
                        PROJECTION,
                        null,
                        null,
                        null
                );
            }
        }
        return  null;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        getLoaderManager().restartLoader(BILLSHARING_LOADER, null, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == BILLSHARING_LOADER)
            mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() >0) {
            if(loader.getId() == DETAIL_LOADER) {
                data.moveToFirst();
                if (descText != null)
                    descText.setText(data.getString(COLUMN_DESC));
                if (amtText != null) {
                    DecimalFormat df = new DecimalFormat("#,###.00");
                    amtText.setText(df.format(data.getDouble(COLUMN_AMT)));
                }
                if (typeText != null)
                    typeText.setText(data.getString(COLUMN_TYPE));

                AppCompatActivity activity = (AppCompatActivity) getActivity();
                Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);
                activity.setSupportActionBar(toolbarView);

                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            else if(loader.getId() == BILLSHARING_LOADER) {
                mCursorAdapter.swapCursor(data);
            }
        }
    }
}
