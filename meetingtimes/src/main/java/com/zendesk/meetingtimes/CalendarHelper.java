package com.zendesk.meetingtimes;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;

/**
 * Created by barry on 16/02/2014.
 */
public class CalendarHelper implements LoaderManager.LoaderCallbacks<Cursor> {

    //
    private static final int CALENDARS_LOADER_ID = 0;
    public static final String HELPER = "helper";

    private final Context mContext;

    public CalendarHelper(LoaderManager loaderManager, Context context) {
        mContext = context;
        loaderManager.initLoader(CALENDARS_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (CALENDARS_LOADER_ID == id) {
            return createCalendarsLoader();
        } else {
            return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(HELPER, "load finished");

        if (CALENDARS_LOADER_ID == loader.getId()) {
            Log.d(HELPER, "handling calendars loader onLoadFinished()");

            final int PROJECTION_ID_INDEX = 0;
            final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
            final int PROJECTION_DISPLAY_NAME_INDEX = 2;
            final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

            while (data.moveToNext()) {
                Log.d(HELPER, "+----+----+----+----+----+----+");
                Log.d(HELPER, "ID           : " + data.getLong(PROJECTION_ID_INDEX));
                Log.d(HELPER, "AC Name      : " + data.getString(PROJECTION_ACCOUNT_NAME_INDEX));
                Log.d(HELPER, "Display Name : " + data.getString(PROJECTION_DISPLAY_NAME_INDEX));
                Log.d(HELPER, "Owner AC Idx : " + data.getString(PROJECTION_OWNER_ACCOUNT_INDEX));
                Log.d(HELPER, "Owner AC Typ : " + data.getString(4));
                Log.d(HELPER, "Name         : " + data.getString(5));
                Log.d(HELPER, "Disp name    : " + data.getString(6));
                Log.d(HELPER, "Sync         : " + data.getString(7));
                Log.d(HELPER, "TZ           : " + data.getString(8));
                Log.d(HELPER, "+----+----+----+----+----+----+\n");
            }

        } else {
            Log.d(HELPER, "onloaderfinished not handled yet for this id");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(HELPER, "load reset");
    }

    private Loader<Cursor> createCalendarsLoader() {

        final String[] eventProjection = new String[] {
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
                CalendarContract.Calendars.ACCOUNT_TYPE,                  // 4
                CalendarContract.Calendars.NAME,                          // 5
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 6
                CalendarContract.Calendars.SYNC_EVENTS,                   // 7
                CalendarContract.Calendars.CALENDAR_TIME_ZONE
        };

        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " LIKE ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";

        String[] selectionArgs = new String[] {"%@zendesk.com", "com.google"};

        CursorLoader loader =
                new CursorLoader(
                        mContext,
                        CalendarContract.Calendars.CONTENT_URI,
                        eventProjection,
                        selection,
                        selectionArgs,
                        null
                );

        Log.d(HELPER, "created loader");
        return loader;
    }
}
