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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanResult != null) {
            String uri = scanResult.getContents();

            if (uri.startsWith("zencal://")) {
                Intent calendarDisplay = new Intent(LauncherActivity.this, MainActivity.class);
                calendarDisplay.putExtra("zencal", uri);
                startActivity(calendarDisplay);
                finish();
            }
        }
    }
}
