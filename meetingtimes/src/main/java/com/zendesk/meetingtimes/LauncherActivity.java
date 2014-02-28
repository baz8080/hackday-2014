package com.zendesk.meetingtimes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by barry on 24/02/2014.
 */
public class LauncherActivity extends Activity {

    public static final String LARGE_ROOM =
            "zendesk.com_2d3735323338373136393736@resource.calendar.google.com";

    public static final String SMALL_ROOM =
            "zendesk.com_2d38373936353133312d363634@resource.calendar.google.com";

    public static final String EXTRA_ZENCAL_URI = "zencal";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        IntentIntegrator integrator = new IntentIntegrator(this);

        //integrator.initiateScan();

        // Hack the calendar uri for maximum speed!
        launchCalendarDisplay(LARGE_ROOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanResult != null) {
            String uri = scanResult.getContents();

            launchCalendarDisplay(uri);
        }
    }

    private void launchCalendarDisplay(String uri) {
        Intent calendarDisplay = new Intent(LauncherActivity.this, MainActivity.class);
        calendarDisplay.putExtra(EXTRA_ZENCAL_URI, uri);
        startActivity(calendarDisplay);
        finish();
    }
}
