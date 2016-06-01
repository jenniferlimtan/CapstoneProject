package com.example.android.housebillsplitter;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.housebillsplitter.data.HouseBillSplitterContract;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

public class HousemateFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private final int LOADER_ID = 70;
    private static final int RC_SIGN_IN = 9001;
    private static final int ISOWNER = 1;

    private static final String[] FROM_IDS = {
            HouseBillSplitterContract.HousemateEntry.COLUMN_NAME,
            HouseBillSplitterContract.HousemateEntry.COLUMN_PHOTO,
            HouseBillSplitterContract.HousemateEntry.COLUMN_EMAIL,
            HouseBillSplitterContract.HousemateEntry.COLUMN_ISOWNER
    };

    private final static int[] TO_IDS = {
            R.id.housemate_name,
            R.id.profile_image,
            R.id.email,
            R.id.isowner
    };

    private static final String[] PROJECTION = {
            HouseBillSplitterContract.HousemateEntry.COLUMN_ID,
            HouseBillSplitterContract.HousemateEntry.COLUMN_NAME,
            HouseBillSplitterContract.HousemateEntry.COLUMN_PHOTO,
            HouseBillSplitterContract.HousemateEntry.COLUMN_EMAIL,
            HouseBillSplitterContract.HousemateEntry.COLUMN_ISOWNER
    };

    private ListView mContactsList;
    private LinearLayout v_google_signin;
    private TextView v_msg_signin;

    private SimpleCursorAdapter mCursorAdapter;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private int selected;
    private static final int SELECTED_ID_INDEX = 0;

    public HousemateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_housemate, container, false);

        v_google_signin = (LinearLayout)rootView.findViewById(R.id.sign_in_header);
        v_msg_signin = (TextView) rootView.findViewById(R.id.sign_in_msg);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            ContentValues housemateValues = new ContentValues();

            housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_CONTACT_ID, 0);
            housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_LOOKUP_KEY, 0);
            housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_NAME, acct.getDisplayName());
            housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_EMAIL, acct.getEmail());
            housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_PHOTO, acct.getPhotoUrl().toString());
            housemateValues.put(HouseBillSplitterContract.HousemateEntry.COLUMN_ISOWNER, 1);

            Uri insertedUri = getContext().getContentResolver().insert(HouseBillSplitterContract.HousemateEntry.CONTENT_URI, housemateValues);
            long inserted_id = ContentUris.parseId(insertedUri);

            if (inserted_id >0) {
                Toast.makeText(getContext(), getString(R.string.msg_welcome, acct.getDisplayName()), Toast.LENGTH_LONG).show();
            }
            v_google_signin.setVisibility(View.INVISIBLE);
        } else {
            v_msg_signin.setText(getString(R.string.msg_signin_failed));
            v_google_signin.setVisibility(View.VISIBLE);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContactsList =  (ListView) getActivity().findViewById(R.id.housemate_list);
        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.housemate_list_item,
                null,
                FROM_IDS, TO_IDS,
                0){

            @Override
            public void setViewText(TextView v, String text) {
                if (v.getId() == R.id.isowner) {
                    RelativeLayout parent =  ((RelativeLayout) v.getParent());
                    if(String.valueOf(ISOWNER).equals(text)) {
                        parent.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryLight));
                        getActivity().findViewById(R.id.sign_in_header).setVisibility(View.INVISIBLE);
                    }
                    else {
                        parent.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
                super.setViewText(v, text);
            }

            @Override
            public void setViewImage(ImageView v, String value) {
                //super.setViewImage(v, value);
                if(value!= null && !value.isEmpty())
                    Picasso.with(getActivity()).load(value).into(v);
            }
        };
        mContactsList.setAdapter(mCursorAdapter);
        mContactsList.setOnItemClickListener(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                HouseBillSplitterContract.HousemateEntry.CONTENT_URI,
                PROJECTION,
                null,
                null,
                HouseBillSplitterContract.HousemateEntry.COLUMN_ISOWNER +" DESC ,"+ HouseBillSplitterContract.HousemateEntry.COLUMN_NAME
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
    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        selected = position;
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.permission_delete_housemate)
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(delete()){
                            Toast.makeText(getContext(), getString(R.string.msg_deleted), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(), getString(R.string.msg_delete_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    public boolean delete(){
        Cursor c = mCursorAdapter.getCursor();
        c.moveToPosition(selected);
        int billsharing_rowsDeleted  = getContext().getContentResolver().delete(HouseBillSplitterContract.BillSharingEntry.CONTENT_URI,
                HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID +" = ?",
                new String[]{c.getString(SELECTED_ID_INDEX)});

        int rowsDeleted  = getContext().getContentResolver().delete(HouseBillSplitterContract.HousemateEntry.CONTENT_URI,
                HouseBillSplitterContract.HousemateEntry.COLUMN_ID +" = ?",
                new String[]{c.getString(SELECTED_ID_INDEX)});

        getActivity().findViewById(R.id.sign_in_header).setVisibility(View.VISIBLE);
        return rowsDeleted>0;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
