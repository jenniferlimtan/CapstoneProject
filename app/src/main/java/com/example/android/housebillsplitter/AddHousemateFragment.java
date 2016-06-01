package com.example.android.housebillsplitter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.housebillsplitter.data.HouseBillSplitterContract;

public class AddHousemateFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private final int LOADER_ID = 10;

    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                    ContactsContract.CommonDataKinds.Email.ADDRESS
    };

    private final static int[] TO_IDS = {
            R.id.housemate_name,
            R.id.profile_image,
            R.id.email
    };

    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.CommonDataKinds.Email.ADDRESS
    };

    private static final int CONTACT_ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int PHOTO_THUMBNAIL_URI_INDEX = 3;
    private static final int EMAIL_INDEX = 4;

    private static final String SELECTION = ContactsContract.Data.MIMETYPE +  "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' AND " +
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?");

    ListView mContactsList;
    private EditText searchText;

    private SimpleCursorAdapter mCursorAdapter;

    private String mSearchString;
    private String[] mSelectionArgs = { mSearchString };

    public AddHousemateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_housemate, container, false);
        searchText = (EditText) rootView.findViewById(R.id.searchText);
        rootView.findViewById(R.id.searchButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSearchString = searchText.getText().toString();
                        AddHousemateFragment.this.restartLoader();
                    }
                }
        );
        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContactsList =  (ListView) getActivity().findViewById(R.id.add_housemate_list);
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.housemate_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        mContactsList.setAdapter(mCursorAdapter);
        mContactsList.setOnItemClickListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSearchString = "%" + mSearchString + "%";
        mSelectionArgs[0] = mSearchString;
        return new CursorLoader(
                getActivity(),
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = mCursorAdapter.getCursor();
        c.moveToPosition(position);
        ContentValues housemateValues = new ContentValues();

        housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_CONTACT_ID, c.getString(CONTACT_ID_INDEX));
        housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_LOOKUP_KEY, c.getString(LOOKUP_KEY_INDEX));
        housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_NAME, c.getString(NAME_INDEX));
        housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_EMAIL, c.getString(EMAIL_INDEX));
        housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_PHOTO, c.getString(PHOTO_THUMBNAIL_URI_INDEX));
        housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_ISOWNER, 0);

        try {
            Uri insertedUri = getContext().getContentResolver().insert(HouseBillSplitterContract.HousemateEntry.CONTENT_URI, housemateValues);
            long inserted_id = ContentUris.parseId(insertedUri);

            if (inserted_id >0) {
                Toast.makeText(getContext(), getString(R.string.msg_saved), Toast.LENGTH_LONG).show();
                NavUtils.navigateUpFromSameTask(getActivity());
            }
        }catch (android.database.SQLException e){
            Toast.makeText(getContext(), getString(R.string.msg_save_failed), Toast.LENGTH_LONG).show();
        }
    }
}
