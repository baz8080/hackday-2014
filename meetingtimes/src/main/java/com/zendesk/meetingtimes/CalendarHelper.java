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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class CalendarHelper {

    //
    private static final int CALENDARS_LOADER_ID = 0;
    private static final int EVENTS_LOADER_ID = 1;

    public static final String LOG_TAG = "helper";
    public static final String CALENDAR_UID = "calendar_identifier";
    public static final String CALENDAR_DB_ID = "calendar_id";

    private final Context mContext;

    private LoaderManager mLoaderManager;
    private EventsListener mEventsListener;

    public CalendarHelper(LoaderManager loaderManager, Context context) {
        mContext = context;
        mLoaderManager = loaderManager;
    }

    static interface EventsListener {
        void onEventsUpdated(List<Event> events);
    }

    public void subscribeToEvents(long calendarId, EventsListener listener) {
        this.mEventsListener = listener;

        Bundle args = new Bundle();
        args.putLong(CALENDAR_DB_ID, calendarId);

        mLoaderManager.initLoader(EVENTS_LOADER_ID, args, new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return createEventsLoader(args.getLong(CALENDAR_DB_ID));
            }

            @Override
            public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

                List<Event> events = getEventsFromCursor(cursor);

                if (mEventsListener != null) {
                    mEventsListener.onEventsUpdated(events);
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> cursorLoader) {

            }
        });
    }

    public void getCalendar(String calendarIdentifier, final Callback<CalendarModel> callback) {

        Bundle bundle = new Bundle();
        bundle.putString(CALENDAR_UID, calendarIdentifier);

        mLoaderManager.initLoader(CALENDARS_LOADER_ID, bundle, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return createCalendarsLoader(args.getString(CALENDAR_UID));
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                Log.d(LOG_TAG, "handling calendars loader onLoadFinished()");

                final int PROJECTION_ID_INDEX = 0;
                final int PROJECTION_DISPLAY_NAME = 2;

                if (data.getCount() != 1) {
                    Toast.makeText(mContext, "Couldn't find the database", Toast.LENGTH_LONG).show();
                } else {
                    data.moveToNext();

                    CalendarModel calendarModel = new CalendarModel();
                    calendarModel.setId(data.getLong(PROJECTION_ID_INDEX));
                    calendarModel.setDisplayName(data.getString(PROJECTION_DISPLAY_NAME));

                    callback.onResult(calendarModel);
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
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        };

        String selection =
                "(" +
                "(" + CalendarContract.Calendars.ACCOUNT_NAME + " LIKE ?)" +
                "AND (" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) " +
                "AND (" + CalendarContract.Calendars.OWNER_ACCOUNT +  " = ?)" +
                ")";

        String[] selectionArgs = new String[] {"%@zendesk.com", "com.google", calendarIdentifier};

        String sortOrder = null;

        CursorLoader loader =
                new CursorLoader(
                        mContext,
                        CalendarContract.Calendars.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                );

        Log.d(LOG_TAG, "created loader");
        return loader;
    }

    private Loader<Cursor> createEventsLoader(long calendarId) {

        Log.i(LOG_TAG, "Looking for events in calendar id: " + calendarId);

        final String[] projection = new String[] {
                CalendarContract.Events.ORGANIZER,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.EVENT_TIMEZONE,
                CalendarContract.Events.EVENT_END_TIMEZONE,
                CalendarContract.Events.RRULE,
                CalendarContract.Events.VISIBLE,
                CalendarContract.Events.LAST_DATE,
                CalendarContract.Events.STATUS,
                CalendarContract.Events.RDATE
        };

        String selection = "(" + CalendarContract.Events.CALENDAR_ID  + " = ?)";
        String[] selectionArgs = new String[] { String.valueOf(calendarId) };

        String sortOrder = CalendarContract.Events.DTSTART + " ASC";

        CursorLoader loader =
                new CursorLoader(
                        mContext,
                        CalendarContract.Events.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                );

        return loader;
    }

    private static List<Event> getEventsFromCursor(Cursor cursor) {

        final int PROJECTION_ORGANISER_IDX = 0;
        final int PROJECTION_TITLE_IDX = 1;
        final int PROJECTION_LOCATION_IDX = 2;
        final int PROJECTION_DESCRIPTION_IDX = 3;
        final int PROJECTION_START_TIME_IDX = 4;
        final int PROJECTION_END_TIME_IDX = 5;
        final int PROJECTION_START_TIMEZONE_IDX = 6;
        final int PROJECTION_END_TIMEZONE_IDX = 7;
        final int PROJECTION_RRULE_IDX = 8;
        final int PROJECTION_HIDDEN_IDX = 9;
        final int PROJECTION_LAST_DATE_IDX = 10;
        final int PROJECTION_STATUS_IDX = 11;
        final int PROJECTION_RDATE_IDX = 12;

        List<Event> events = new ArrayList<Event>();

        /**
         * TODO
         * Would probably be more bueno to filter these in the SQL
         */

        Calendar todayCalendar = Calendar.getInstance();

        todayCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);

        long startOfDay = todayCalendar.getTimeInMillis();

        todayCalendar.set(Calendar.HOUR_OF_DAY, 23);
        todayCalendar.set(Calendar.MINUTE, 59);
        todayCalendar.set(Calendar.SECOND, 59);

        long endOfDay = todayCalendar.getTimeInMillis();

        while (cursor.moveToNext()) {

            Event event = new Event();

            event.setOrganiserEmail(cursor.getString(PROJECTION_ORGANISER_IDX));
            event.setTitle(cursor.getString(PROJECTION_TITLE_IDX));
            event.setLocation(cursor.getString(PROJECTION_LOCATION_IDX));
            event.setDescription(cursor.getString(PROJECTION_DESCRIPTION_IDX));

            long startTime = cursor.getLong(PROJECTION_START_TIME_IDX);
            event.setStartMillisecondsUtc(startTime);
            event.setStartTimezone(cursor.getString(PROJECTION_START_TIMEZONE_IDX));

            long endTime = cursor.getLong(PROJECTION_END_TIME_IDX);
            event.setEndMillisecondsUtc(endTime);
            event.setEndTimeZone(cursor.getString(PROJECTION_END_TIMEZONE_IDX));

            event.setRepeatRule(cursor.getString(PROJECTION_RRULE_IDX));
            event.setHidden(cursor.getInt(PROJECTION_HIDDEN_IDX) == 1);
            event.setLastDate(cursor.getLong(PROJECTION_LAST_DATE_IDX));
            event.setStatus(cursor.getInt(PROJECTION_STATUS_IDX));
            event.setRepeatDates(cursor.getString(PROJECTION_RDATE_IDX));


            if (startTime >= startOfDay && startTime <= endOfDay) {
                events.add(event);
            }
        }

        return events;
    }

}
