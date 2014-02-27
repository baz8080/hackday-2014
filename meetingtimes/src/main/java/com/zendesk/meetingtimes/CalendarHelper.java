package com.zendesk.meetingtimes;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;


public class CalendarHelper {

    //
    private static final int CALENDARS_LOADER_ID = 0;
    private static final int EVENTS_LOADER_ID = 1;

    public static final String LOG_TAG = "helper";
    public static final String CALENDAR_IDENTIFIER = "calendar_identifier";

    private final Context mContext;

    private LoaderManager mLoaderManager;

    public CalendarHelper(LoaderManager loaderManager, Context context) {
        mContext = context;
        mLoaderManager = loaderManager;
    }

    public void getCalendarId(String calendarIdentifier, final Callback<Long> callback) {

        Bundle bundle = new Bundle();
        bundle.putString(CALENDAR_IDENTIFIER, calendarIdentifier);

        mLoaderManager.initLoader(CALENDARS_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return createCalendarsLoader(args.getString(CALENDAR_IDENTIFIER));
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                Log.d(LOG_TAG, "handling calendars loader onLoadFinished()");

                final int PROJECTION_ID_INDEX = 0;

                if (data.getCount() != 1) {
                    Toast.makeText(mContext, "Couldn't find the database", Toast.LENGTH_LONG).show();
                } else {
                    data.moveToNext();
                    callback.onResult(data.getLong(PROJECTION_ID_INDEX));
                    mLoaderManager.destroyLoader(CALENDARS_LOADER_ID);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                /**
                 * Not too sure we'll need this
                 */
            }
        });
    }

    private Loader<Cursor> createCalendarsLoader(String calendarIdentifier) {

        final String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.OWNER_ACCOUNT
        };

        String selection =
                "(" +
                "(" + CalendarContract.Calendars.ACCOUNT_NAME + " LIKE ?)" +
                "AND (" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) " +
                "AND (" + CalendarContract.Calendars.OWNER_ACCOUNT +  " = ?)" +
                ")";

        String[] selectionArgs = new String[] {"%@zendesk.com", "com.google", calendarIdentifier};

        CursorLoader loader =
                new CursorLoader(
                        mContext,
                        CalendarContract.Calendars.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null
                );

        Log.d(LOG_TAG, "created loader");
        return loader;
    }

    private Loader<Cursor> createEventsLoader(long calendarId) {

        final String[] projection = new String[] {
                CalendarContract.Events.ORGANIZER,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_TIMEZONE,
                CalendarContract.Events.EVENT_END_TIMEZONE
        };

        String selection = "(" + CalendarContract.Events.CALENDAR_ID  + " = ?)";
        String[] selectionArgs = new String[]{String.valueOf(calendarId)};

        CursorLoader loader =
                new CursorLoader(
                        mContext,
                        CalendarContract.Events.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null
                );

        return loader;
    }

}
