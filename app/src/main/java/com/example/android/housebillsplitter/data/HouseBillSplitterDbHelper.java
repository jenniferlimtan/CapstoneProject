
package com.example.android.housebillsplitter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HouseBillSplitterDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "housebillsplitter.db";

    public HouseBillSplitterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_HOUSEMATE_TABLE = "CREATE TABLE " + HouseBillSplitterContract.HousemateEntry.TABLE_NAME + " (" +
                HouseBillSplitterContract.HousemateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HouseBillSplitterContract.HousemateEntry.COLUMN_CONTACT_ID + " TEXT NOT NULL, " +
                HouseBillSplitterContract.HousemateEntry.COLUMN_LOOKUP_KEY + " TEXT NOT NULL, " +
                HouseBillSplitterContract.HousemateEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                HouseBillSplitterContract.HousemateEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                HouseBillSplitterContract.HousemateEntry.COLUMN_PHOTO + " TEXT," +
                HouseBillSplitterContract.HousemateEntry.COLUMN_ISOWNER + " INTEGER " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_HOUSEMATE_TABLE);

        final String SQL_CREATE_BILLITEM_TABLE = "CREATE TABLE " + HouseBillSplitterContract.BillItemEntry.TABLE_NAME + " (" +
                HouseBillSplitterContract.BillItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HouseBillSplitterContract.BillItemEntry.COLUMN_DESC + " TEXT NOT NULL, " +
                HouseBillSplitterContract.BillItemEntry.COLUMN_AMT + " REAL NOT NULL, " +
                HouseBillSplitterContract.BillItemEntry.COLUMN_TYPE + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_BILLITEM_TABLE);

        final String SQL_CREATE_BILLSHARING_TABLE = "CREATE TABLE " + HouseBillSplitterContract.BillSharingEntry.TABLE_NAME + " (" +
                HouseBillSplitterContract.BillSharingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_ID + " INTEGER NOT NULL, " +
                HouseBillSplitterContract.BillSharingEntry.COLUMN_BILLITEM_ID + " INTEGER NOT NULL, " +
                HouseBillSplitterContract.BillSharingEntry.COLUMN_PERCENTAGE + " INTEGER NOT NULL, " +
                HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT + " REAL NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_BILLSHARING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HouseBillSplitterContract.HousemateEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HouseBillSplitterContract.BillItemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HouseBillSplitterContract.BillSharingEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
