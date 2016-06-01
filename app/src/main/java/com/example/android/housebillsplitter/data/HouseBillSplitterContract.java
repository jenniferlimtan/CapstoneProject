
package com.example.android.housebillsplitter.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class HouseBillSplitterContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.housebillsplitter";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HOUSEMATE = "housemate";
    public static final String PATH_BILLITEM = "billitem";
    public static final String PATH_BILLSHARING = "billsharing";

    public static final class HousemateEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HOUSEMATE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HOUSEMATE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HOUSEMATE;

        public static final String TABLE_NAME = "housemate";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_CONTACT_ID = "contact_id";
        public static final String COLUMN_LOOKUP_KEY = "lookup";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHOTO = "photo_thumbnail_uri";
        public static final String COLUMN_ISOWNER = "isowner";

        public static Uri buildHousemateUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class BillItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BILLITEM).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BILLITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BILLITEM;

        public static final String TABLE_NAME = "billitem";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DESC = "description";
        public static final String COLUMN_AMT = "amount";
        public static final String COLUMN_TYPE = "type";

        public static Uri buildBillItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static String getIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /* Inner class that defines the table contents of the location table */
    public static final class BillSharingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BILLSHARING).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BILLSHARING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BILLSHARING;

        public static final String TABLE_NAME = "billsharing";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_HOUSEMATE_ID = "housemate_id";
        public static final String COLUMN_BILLITEM_ID = "billitem_id";
        public static final String COLUMN_PERCENTAGE = "percentage";
        public static final String COLUMN_AMT = "amount";
        public static final String COLUMN_HOUSEMATE_NAME = "housemate_name";

        public static Uri buildBillSharingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildBillSharingByHousemateIDUri(String housemate_id) {
            return CONTENT_URI.buildUpon().appendPath(PATH_HOUSEMATE)
                    .appendPath(housemate_id).build();
        }

        public static Uri buildBillSharingByBillItemIDUri(String bill_item_id) {
            return CONTENT_URI.buildUpon().appendPath(PATH_BILLITEM)
                    .appendPath(bill_item_id).build();
        }

        public static String getBillItemIDFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getHousemateIDFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
}
