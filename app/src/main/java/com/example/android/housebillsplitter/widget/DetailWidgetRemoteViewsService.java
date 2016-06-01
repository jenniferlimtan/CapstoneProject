package com.example.android.housebillsplitter.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.housebillsplitter.HousemateBillDetailActivity;
import com.example.android.housebillsplitter.HousemateBillDetailFragment;
import com.example.android.housebillsplitter.R;
import com.example.android.housebillsplitter.data.HouseBillSplitterContract;

import java.text.DecimalFormat;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] PROJECTION = {
            HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_ID + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_ID,
            HouseBillSplitterContract.HousemateEntry.TABLE_NAME + "." + HouseBillSplitterContract.HousemateEntry.COLUMN_NAME + " as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_HOUSEMATE_NAME,
            "SUM("+ HouseBillSplitterContract.BillSharingEntry.TABLE_NAME +"." + HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT + ") as " + HouseBillSplitterContract.BillSharingEntry.COLUMN_AMT
    };

    // these indices must match the projection
    static final int INDEX_COLUMN_ID = 0;
    static final int INDEX_COLUMN_NAME = 1;
    static final int INDEX_COLUMN_AMT= 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

               data = getContentResolver().query(HouseBillSplitterContract.BillSharingEntry.CONTENT_URI,
                        PROJECTION,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);
                DecimalFormat df = new DecimalFormat(getString(R.string.amount_format));
                String total_amt = df.format(data.getDouble(INDEX_COLUMN_AMT));

                views.setTextViewText(R.id.widget_housemate_name, data.getString(INDEX_COLUMN_NAME));
                views.setTextViewText(R.id.widget_billsharing_amt,total_amt);

                final Intent fillInIntent = new Intent();

                fillInIntent.putExtra(HousemateBillDetailFragment.KEY_HOUSEMATE_ID, data.getString(INDEX_COLUMN_ID));
                fillInIntent.putExtra(HousemateBillDetailActivity.KEY_HOUSEMATE_NAME, data.getString(INDEX_COLUMN_NAME));
                fillInIntent.putExtra(HousemateBillDetailFragment.KEY_TOTAL_AMT, total_amt);

                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_COLUMN_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
