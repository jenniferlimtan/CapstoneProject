package com.example.android.housebillsplitter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.housebillsplitter.data.HouseBillSplitterContract;
import com.example.android.housebillsplitter.data.HouseBillSplitterProvider;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class EditBillItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EDIT_LOADER = 50;

    public static final String KEY_BILLITEM_ID = "bill_item_id";
    private static final String KEY_BILLITEM_DESC = "billitem_desc";
    private static final String KEY_BILLITEM_AMT = "billitem_amt";
    private static final String KEY_BILLITEM_TYPE = "billitem_type";
    private static final String KEY_BILLSHARING_LIST = "billsharing_list";

    private static final String[] EDIT_COLUMNS = {
            HouseBillSplitterContract.BillItemEntry.COLUMN_ID,
            HouseBillSplitterContract.BillItemEntry.COLUMN_DESC,
            HouseBillSplitterContract.BillItemEntry.COLUMN_AMT,
            HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE
    };

    private static final String[] PROJECTION = {
            HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +"."+ HouseBillSplitterContract.BillSharingEntry.COLUMN_ID ,
            HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_ID + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID,
            HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_NAME + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_NAME,
            HouseBillSplitterContract.BillSharingEntry.COLUMN_PERCENTAGE
    };

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_DESC = 1;
    private static final int COLUMN_AMT = 2;
    private static final int COLUMN_TYPE = 3;
    private static final int COLUMN_HOUSEMATE_ID = 1;
    private static final int COLUMN_HOUSEMATE_NAME = 2;
    private static final int COLUMN_PERCENTAGE = 3;

    private ListView mBillSharingList;
    private EditText descText;
    private EditText amtText;
    private Spinner typeText;

    private DecimalFormat df ;

    private BillSharingAdapter mBillSharingAdapter;
    private ArrayList<ParcelableBillSharing> billsharing_list;

    private String bill_item_id;
    private String billitem_desc;
    private String billitem_amt;
    private String billitem_type;

    public EditBillItemFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_billitem, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            bill_item_id = arguments.getString(KEY_BILLITEM_ID);
        }

        if(savedInstanceState != null) {
            billsharing_list = savedInstanceState.getParcelableArrayList(KEY_BILLSHARING_LIST);
            billitem_desc = savedInstanceState.getString(KEY_BILLITEM_DESC);
            billitem_amt = savedInstanceState.getString(KEY_BILLITEM_DESC);
            billitem_type = savedInstanceState.getString(KEY_BILLITEM_DESC);
        } else {
            billsharing_list = new ArrayList<ParcelableBillSharing>();
            loadBillSharing();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_bill_item, container, false);

        descText = (EditText) rootView.findViewById(R.id.billitem_desc);
        amtText = (EditText) rootView.findViewById(R.id.billitem_amt);
        typeText = (Spinner) rootView.findViewById(R.id.billitem_type);

        if(savedInstanceState != null) {
            descText.setText(billitem_desc);
            amtText.setText(billitem_amt);
            typeText.setSelection(((ArrayAdapter<String>)typeText.getAdapter()).getPosition(billitem_type));
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        Toolbar toolbarView = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbarView);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        df = new DecimalFormat(getString(R.string.amount_format));
        if(savedInstanceState == null)
            getLoaderManager().initLoader(EDIT_LOADER, null, this);

        mBillSharingList =  (ListView) getActivity().findViewById(R.id.billsharing_list);
        mBillSharingAdapter = new BillSharingAdapter();
        mBillSharingList.setAdapter(mBillSharingAdapter);
        mBillSharingList.setEnabled(false);

        final Spinner spinner = (Spinner) getActivity().findViewById(R.id.billitem_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.split_type_array, R.layout.type_spinner_item);
        adapter.setDropDownViewResource(R.layout.type_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals(getString(R.string.split_by_percent)))
                    mBillSharingList.setVisibility(View.VISIBLE);
                else
                    mBillSharingList.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelableArrayList(KEY_BILLSHARING_LIST, billsharing_list);
        outState.putString(KEY_BILLITEM_DESC, billitem_desc);
        outState.putString(KEY_BILLITEM_AMT, billitem_amt);
        outState.putString(KEY_BILLITEM_TYPE, billitem_desc);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if  (id == R.id.action_save) {
             if(validateUserInput())
                return saveBillItem();
        }else if (id == R.id.action_cancel){
            NavUtils.navigateUpFromSameTask(getActivity());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != bill_item_id) {
                return new CursorLoader(
                        getActivity(),
                        HouseBillSplitterContract.BillItemEntry.buildBillItemUri(Long.parseLong(bill_item_id)),
                        EDIT_COLUMNS,
                        null,
                        null,
                        null
                );
        }
        return  null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() >0) {
               data.moveToFirst();
                if (descText != null) {
                    descText.setText(data.getString(COLUMN_DESC));
                    billitem_desc = data.getString(COLUMN_DESC);
                }
                if (amtText != null) {
                    amtText.setText(df.format(data.getDouble(COLUMN_AMT)));
                    billitem_amt = amtText.toString();
                }
                if (typeText != null) {
                    billitem_type = data.getString(COLUMN_TYPE);
                    typeText.setSelection(((ArrayAdapter<String>)typeText.getAdapter()).getPosition(billitem_type));
                }
        }
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(EDIT_LOADER, null, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadBillSharing(){
            Cursor data = getActivity().getContentResolver().query(HouseBillSplitterContract.BillSharingEntry.buildBillSharingByBillItemIDUri(bill_item_id == null || bill_item_id.isEmpty()? "0": bill_item_id), PROJECTION, null, null, null);
            while (data.moveToNext()) {
                billsharing_list.add(new ParcelableBillSharing(data.getString(COLUMN_ID), data.getString(COLUMN_HOUSEMATE_ID), data.getString(COLUMN_HOUSEMATE_NAME), data.getInt(COLUMN_PERCENTAGE)));
            }
    }

    public class BillSharingAdapter extends ArrayAdapter<ParcelableBillSharing> {

        BillSharingHolder holder=null;

        public BillSharingAdapter() {
            super(getActivity(), R.layout.billsharing_edit_list_item);
        }

        public class BillSharingHolder
        {
            TextView v_billsharing_id;
            TextView v_housemate_id;
            TextView v_housemate_name;
            EditText v_percentage;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView==null)
            {
                LayoutInflater infl = LayoutInflater.from(getContext());
                convertView=infl.inflate(R.layout.billsharing_edit_list_item, parent, false);
                holder=new BillSharingHolder();
                holder.v_billsharing_id=(TextView) convertView.findViewById(R.id.billsharing_id);
                holder.v_housemate_id=(TextView) convertView.findViewById(R.id.housemate_id);
                holder.v_housemate_name=(TextView) convertView.findViewById(R.id.housemate_name);
                holder.v_percentage=(EditText) convertView.findViewById(R.id.percentage);
                convertView.setTag(holder);
            }
            else
            {
                holder=(BillSharingHolder) convertView.getTag();
            }

            ParcelableBillSharing e= getItem(position);

            holder.v_billsharing_id.setText(e.getBillsharing_id());
            holder.v_housemate_id.setText(e.getHousemate_id());
            holder.v_housemate_name.setText(e.getHousemate_name());
            holder.v_percentage.setText(e.getPercentageAsString());
            holder.v_percentage.setTag(position);

            holder.v_percentage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        final int position = (Integer)v.getTag();
                        final EditText percentage = (EditText) v;
                        billsharing_list.get(position).setPercentage(percentage.getText().toString());
                    }
                }
            });

            holder.v_percentage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        final int position =  (Integer)v.getTag();
                        final EditText percentage = (EditText) v;
                        billsharing_list.get(position).setPercentage(percentage.getText().toString());
                    }
                    return false;
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return billsharing_list.size();
        }

        @Override
        public ParcelableBillSharing getItem(int position) {
            // TODO Auto-generated method stub
            return billsharing_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public void clear(){
            billsharing_list.clear();
            notifyDataSetChanged();
        }


        public void add(ParcelableBillSharing item){
            billsharing_list.add(item);
            notifyDataSetChanged();
        }
    }

    private boolean validateUserInput(){
        if(descText.getText().toString().trim().isEmpty() ) {
            Toast.makeText(getContext(), getString(R.string.msg_mandatory, descText.getHint()), Toast.LENGTH_LONG).show();
            return false;
        }
        else if(amtText.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(),getString(R.string.msg_mandatory,amtText.getHint()), Toast.LENGTH_LONG).show();
            return false;
        }
        else if(amtText.getText().toString().trim().replace(".","").isEmpty()){
            Toast.makeText(getContext(),getString(R.string.msg_invalid_input,amtText.getHint()), Toast.LENGTH_LONG).show();
            return false;
        }
        else if(typeText.getSelectedItem().toString().equals(getString(R.string.split_by_percent))){
            int sumPercentage = 0;
            for (int i = 0; i < billsharing_list.size(); i++) {
                ParcelableBillSharing data = billsharing_list.get(i);
                sumPercentage = sumPercentage + data.getPercentage();
            }

            if (billsharing_list.size() > 0 && sumPercentage != 100) {
                Toast.makeText(getContext(), getString(R.string.msg_total_percentage), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private boolean saveBillItem(){
        boolean success = false;
        String selectedType= typeText.getSelectedItem().toString();
        double amt = Double.parseDouble(amtText.getText().toString().replace(",",""));

        ContentValues billitemValues = new ContentValues();
        billitemValues.put(HouseBillSplitterContract.BillItemEntry.COLUMN_DESC, descText.getText().toString());
        billitemValues.put(HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE, selectedType);
        billitemValues.put(HouseBillSplitterContract.BillItemEntry.COLUMN_AMT, amt);

        if(bill_item_id != null && !bill_item_id.isEmpty()) {
            int rowsUpdated = getContext().getContentResolver().update(HouseBillSplitterContract.BillItemEntry.CONTENT_URI, billitemValues,
                    HouseBillSplitterContract.BillItemEntry.COLUMN_ID + " = ?",
                    new String[]{bill_item_id});
            if(rowsUpdated >0) {
                success= true;
            }

        } else{
            Uri insertedBillItemUri = getContext().getContentResolver().insert(HouseBillSplitterContract.BillItemEntry.CONTENT_URI, billitemValues);
            long inserted_bill_item_id = ContentUris.parseId(insertedBillItemUri);
            if (inserted_bill_item_id >0) {
                bill_item_id = String.valueOf(inserted_bill_item_id);
                success = true;
            }
        }

        if(!success) {
            Toast.makeText(getContext(), getString(R.string.msg_save_failed), Toast.LENGTH_LONG).show();
        }
        else{
            double billsharing_amt;
            int percentage;
                for (int i = 0; i < billsharing_list.size(); i++) {
                    ParcelableBillSharing data = billsharing_list.get(i);
                    ContentValues billsharingValues = new ContentValues();

                    if(selectedType.equals(getString(R.string.split_by_percent))) {
                        billsharing_amt = ((double) data.getPercentage() / 100) * amt;
                        percentage = data.getPercentage();
                    }else {
                        percentage = 0;
                        billsharing_amt = amt/billsharing_list.size();
                    }

                    billsharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID, data.getHousemate_id());
                    billsharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_BILLITEM_ID, bill_item_id);
                    billsharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_PERCENTAGE, percentage);
                    billsharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT, billsharing_amt);

                    if (data.getBillsharing_id() == null || data.getBillsharing_id().isEmpty()) {
                        Uri insertedBillSharingUri = getContext().getContentResolver().insert(HouseBillSplitterContract.BillSharingEntry.CONTENT_URI, billsharingValues);
                        long inserted_bill_sharing_id = ContentUris.parseId(insertedBillSharingUri);

                        if (inserted_bill_sharing_id > 0) {
                            data.setBillsharing_id(String.valueOf(inserted_bill_sharing_id));
                        } else {
                            success = false;
                            break;
                        }
                    } else {
                        int rowsUpdated = getContext().getContentResolver().update(HouseBillSplitterContract.BillSharingEntry.CONTENT_URI, billsharingValues,
                                HouseBillSplitterContract.BillSharingEntry.COLUMN_ID + " = ?",
                                new String[]{data.getBillsharing_id()});
                        if (rowsUpdated == 0) {
                            success = false;
                            break;
                        }
                    }
                }

            if(success) {
                Toast.makeText(getContext(), getString(R.string.msg_saved), Toast.LENGTH_LONG).show();
                NavUtils.navigateUpFromSameTask(getActivity());
            }
            else{
                Toast.makeText(getContext(), getString(R.string.msg_save_failed), Toast.LENGTH_LONG).show();
            }
        }

        HouseBillSplitterProvider.updateWidgets(getContext());
        return success;
    }
}
