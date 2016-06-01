
package com.example.android.housebillsplitter.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.android.housebillsplitter.R;

public class HouseBillSplitterProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private HouseBillSplitterDbHelper mOpenHelper;

    static final int HOUSEMATE = 100;
    static final int BILLITEM = 200;
    static final int BILLITEM_WITH_ID = 300;
    static final int BILLSHARING = 400;
    static final int BILLSHARING_WITH_ID = 500;
    static final int BILLSHARING_WITH_BILLITEM_ID = 600;
    static final int BILLSHARING_WITH_HOUSEMATE_ID = 700;

    private static final SQLiteQueryBuilder housemateQueryBuilder;
    private static final SQLiteQueryBuilder billitemQueryBuilder;
    private static final SQLiteQueryBuilder billsharingQueryBuilder;
    private static final SQLiteQueryBuilder billsharingByBillItemIDQueryBuilder;
    private static final SQLiteQueryBuilder billsharingByHousemateIDQueryBuilder;
    static{
        housemateQueryBuilder = new SQLiteQueryBuilder();
        housemateQueryBuilder.setTables(
                HouseBillSplitterContract.HousemateEntry.TABLE_NAME);

        billitemQueryBuilder = new SQLiteQueryBuilder();
        billitemQueryBuilder.setTables(
                HouseBillSplitterContract.BillItemEntry.TABLE_NAME);

        billsharingQueryBuilder = new SQLiteQueryBuilder();
        billsharingQueryBuilder.setTables(
                HouseBillSplitterContract.HousemateEntry.TABLE_NAME + " LEFT JOIN " +
                        HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +
                        " ON " + HouseBillSplitterContract.BillSharingEntry.TABLE_NAME  +
                        "." + HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID +
                        " = " + HouseBillSplitterContract.HousemateEntry.TABLE_NAME +
                        "." + HouseBillSplitterContract.HousemateEntry._ID);

        billsharingByBillItemIDQueryBuilder = new SQLiteQueryBuilder();
        billsharingByBillItemIDQueryBuilder.setTables(
                HouseBillSplitterContract.HousemateEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +
                        " ON " + HouseBillSplitterContract.BillSharingEntry.TABLE_NAME  +
                        "." + HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID +
                        " = " + HouseBillSplitterContract.HousemateEntry.TABLE_NAME +
                        "." + HouseBillSplitterContract.HousemateEntry._ID + " AND (" +
                        HouseBillSplitterContract.BillSharingEntry.TABLE_NAME+"." +
                        HouseBillSplitterContract.BillSharingEntry.COLUMN_BILLITEM_ID + " = ? OR "+
                        HouseBillSplitterContract.BillSharingEntry.TABLE_NAME+ "." +
                        HouseBillSplitterContract.BillSharingEntry.COLUMN_BILLITEM_ID + " IS NULL) ");

        billsharingByHousemateIDQueryBuilder = new SQLiteQueryBuilder();
        billsharingByHousemateIDQueryBuilder.setTables(
                HouseBillSplitterContract.BillItemEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +
                        " ON " + HouseBillSplitterContract.BillSharingEntry.TABLE_NAME  +
                        "." + HouseBillSplitterContract.BillSharingEntry.COLUMN_BILLITEM_ID +
                        " = " + HouseBillSplitterContract.BillItemEntry.TABLE_NAME +
                        "." + HouseBillSplitterContract.BillItemEntry._ID + " AND (" +
                        HouseBillSplitterContract.BillSharingEntry.TABLE_NAME+"." +
                        HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID + " = ? OR "+
                        HouseBillSplitterContract.BillSharingEntry.TABLE_NAME+ "." +
                        HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID + " IS NULL) ");
    }

    private static final String sBillItemIDSelection =
            HouseBillSplitterContract.BillItemEntry.TABLE_NAME+
                    "." + HouseBillSplitterContract.BillItemEntry.COLUMN_ID + " = ? ";

    public static final String ACTION_DATA_UPDATED =
            "com.example.android.housebillsplitter.ACTION_DATA_UPDATED";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HouseBillSplitterContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, HouseBillSplitterContract.PATH_HOUSEMATE, HOUSEMATE);
        matcher.addURI(authority, HouseBillSplitterContract.PATH_BILLITEM, BILLITEM);
        matcher.addURI(authority, HouseBillSplitterContract.PATH_BILLITEM + "/#", BILLITEM_WITH_ID);
        matcher.addURI(authority, HouseBillSplitterContract.PATH_BILLSHARING, BILLSHARING);
        matcher.addURI(authority, HouseBillSplitterContract.PATH_BILLSHARING + "/" + HouseBillSplitterContract.PATH_BILLITEM+ "/#", BILLSHARING_WITH_BILLITEM_ID);
        matcher.addURI(authority, HouseBillSplitterContract.PATH_BILLSHARING + "/" + HouseBillSplitterContract.PATH_HOUSEMATE+ "/#", BILLSHARING_WITH_HOUSEMATE_ID);
        matcher.addURI(authority, HouseBillSplitterContract.PATH_BILLSHARING+ "/#", BILLSHARING_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new HouseBillSplitterDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HOUSEMATE:
                return HouseBillSplitterContract.HousemateEntry.CONTENT_TYPE;
            case BILLITEM:
                return HouseBillSplitterContract.BillItemEntry.CONTENT_TYPE;
            case BILLITEM_WITH_ID:
                return HouseBillSplitterContract.BillItemEntry.CONTENT_TYPE;
            case BILLSHARING:
                return HouseBillSplitterContract.BillSharingEntry.CONTENT_TYPE;
            case BILLSHARING_WITH_ID:
                return HouseBillSplitterContract.BillSharingEntry.CONTENT_TYPE;
            case BILLSHARING_WITH_BILLITEM_ID:
                return HouseBillSplitterContract.BillSharingEntry.CONTENT_TYPE;
            case BILLSHARING_WITH_HOUSEMATE_ID:
                return HouseBillSplitterContract.BillSharingEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
           case HOUSEMATE: {
                retCursor = housemateQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BILLITEM: {
                retCursor = billitemQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BILLITEM_WITH_ID: {
                retCursor =  billitemQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        sBillItemIDSelection,
                        new String[]{HouseBillSplitterContract.BillItemEntry.getIDFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case BILLSHARING: {
                retCursor =  billsharingQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_ID,
                        null,
                        sortOrder == null ? HouseBillSplitterContract.HousemateEntry.COLUMN_NAME + " ASC": sortOrder
                );
                break;
            }
            case BILLSHARING_WITH_BILLITEM_ID: {
                retCursor =  billsharingByBillItemIDQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        null,
                        new String[]{HouseBillSplitterContract.BillSharingEntry.getBillItemIDFromUri(uri)},
                        null,
                        null,
                        sortOrder == null ? HouseBillSplitterContract.HousemateEntry.COLUMN_NAME + " ASC": sortOrder
                );
                break;
            }
            case BILLSHARING_WITH_HOUSEMATE_ID: {
                retCursor =  billsharingByHousemateIDQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        null,
                        new String[]{HouseBillSplitterContract.BillSharingEntry.getHousemateIDFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case HOUSEMATE: {
                long _id = db.insert(HouseBillSplitterContract.HousemateEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = HouseBillSplitterContract.HousemateEntry.buildHousemateUri(_id);
                    updateEqualSharing(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BILLITEM: {
                long _id = db.insert(HouseBillSplitterContract.BillItemEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HouseBillSplitterContract.BillItemEntry.buildBillItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case BILLSHARING: {
                long _id = db.insert(HouseBillSplitterContract.BillSharingEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = HouseBillSplitterContract.BillSharingEntry.buildBillSharingUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        switch (match) {
            case HOUSEMATE:
                rowsDeleted = db.delete(HouseBillSplitterContract.HousemateEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted >0)
                    updateEqualSharing(0);
                break;
            case BILLITEM:
                rowsDeleted = db.delete(HouseBillSplitterContract.BillItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BILLSHARING:
                rowsDeleted = db.delete(HouseBillSplitterContract.BillSharingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case HOUSEMATE:
                rowsUpdated = db.update(HouseBillSplitterContract.HousemateEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BILLITEM:
                rowsUpdated = db.update(HouseBillSplitterContract.BillItemEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BILLSHARING:
                rowsUpdated = db.update(HouseBillSplitterContract.BillSharingEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case HOUSEMATE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HouseBillSplitterContract.HousemateEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case BILLITEM:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HouseBillSplitterContract.BillItemEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case BILLSHARING:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(HouseBillSplitterContract.BillSharingEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    private void updateEqualSharing(long housemate_id){
        Cursor housemates = housemateQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                new String[]{ "COUNT(*) as NO_OF_HOUSEMATES "},
                null, null, null, null, null);

        if(housemates.moveToFirst()){
            Cursor billitems = billitemQueryBuilder.query(
                    mOpenHelper.getReadableDatabase(),
                    new String[]{
                            HouseBillSplitterContract.BillItemEntry.COLUMN_ID,
                            HouseBillSplitterContract.BillItemEntry.COLUMN_AMT
                    },
                    HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE + " LIKE ? ",
                    new String []{ getContext().getString(R.string.split_equally) },
                    null, null, null
            );

            double amt ;
            String bill_item_id;

            while (billitems.moveToNext()){
                bill_item_id = billitems.getString(0);
                amt = billitems.getDouble(1)/housemates.getDouble(0);

                ContentValues billsharingValues = new ContentValues();
                billsharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT, amt);

                int rowsUpdated = update(HouseBillSplitterContract.BillSharingEntry.CONTENT_URI, billsharingValues,
                        HouseBillSplitterContract.BillSharingEntry.COLUMN_BILLITEM_ID + " = ?",
                        new String[]{bill_item_id});

                if(housemate_id>0){
                    ContentValues newHousemateBillSharingValues = new ContentValues();
                    newHousemateBillSharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID, housemate_id);
                    newHousemateBillSharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_BILLITEM_ID, bill_item_id);
                    newHousemateBillSharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_PERCENTAGE, 0);
                    newHousemateBillSharingValues.put(HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT, amt);
                    getContext().getContentResolver().insert(HouseBillSplitterContract.BillSharingEntry.CONTENT_URI, newHousemateBillSharingValues);
                }
            }
        }
        updateWidgets(getContext());
    }


    public static void updateWidgets(Context ctx) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(ctx.getPackageName());
        ctx.sendBroadcast(dataUpdatedIntent);
    }

}