package com.zendesk.meetingtimes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zendesk.meetingtimes.util.DateUtil;
import com.zendesk.meetingtimes.util.SystemUiHider;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private CalendarHelper mCalendarHelper;

    private List<Event> mEvents;
    private EventsAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        mListView = (ListView) findViewById(R.id.events_list);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


        final TextView currentTime = (TextView) findViewById(R.id.current_time);

        Timer updateTimeTimer = new Timer();

        updateTimeTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                 runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Calendar now = Calendar.getInstance();
                        currentTime.setText(DateUtil.formatTime(now));
                    }
                });
            }
        }, 0, 1000);

        Timer updateStatusTimer = new Timer();

        updateStatusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(CalendarHelper.LOG_TAG, "Update events tick...");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateEvents();
                    }
                });
            }
        }, 0, TimeUnit.MINUTES.toMillis(1));

        mCalendarHelper = new CalendarHelper(getLoaderManager(), this);

        mCalendarHelper.getCalendar(getIntent().getStringExtra(LauncherActivity.EXTRA_ZENCAL_URI), new Callback<CalendarModel>() {
            @Override
            public void onResult(CalendarModel result) {

                TextView roomName = (TextView) findViewById(R.id.room_name);
                roomName.setText(result.getDisplayName());

                long calendarDatabaseId = result.getId();

                mCalendarHelper.subscribeToEvents(calendarDatabaseId, new CalendarHelper.EventsListener() {

                    @Override
                    public void onEventsUpdated(List<Event> events) {
                        mEvents = events;
                        updateEvents();
                    }
                });
            }
        });
    }

    private void updateEvents() {

        if (mEvents == null || mEvents.size() == 0) {
            return;
        }

        mAdapter = new EventsAdapter(this, R.layout.row_event, mEvents);
        mListView.setAdapter(mAdapter);

        /**
         * TODO
         * First I have to see if there's a meeting happening now or not
         */

        final ImageView status = (ImageView) findViewById(R.id.room_status);
        final View availabilityContainer = findViewById(R.id.availability_container);
        final TextView availableAtView = (TextView) findViewById(R.id.available_at);

        long now = System.currentTimeMillis();
        boolean available = true;
        int currentMeetingIndex = 0;

        for (int i = 0; i < mEvents.size() && currentMeetingIndex == 0; i++) {

            Event event = mEvents.get(i);

            if (now >= event.getStartMillisecondsUtc() && now <= event.getEndMillisecondsUtc()) {
                available = false;
                currentMeetingIndex = i;
            }
        }

        if (available) {
            status.setImageResource(R.drawable.zc_available);
            availabilityContainer.setBackgroundColor(getResources().getColor(R.color.available_color));
            availableAtView.setText("Available for ...");
        } else {
            status.setImageResource(R.drawable.zc_unavailable);
            availabilityContainer.setBackgroundColor(getResources().getColor(R.color.unavailable_color));
            availableAtView.setText("Available at ...");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
