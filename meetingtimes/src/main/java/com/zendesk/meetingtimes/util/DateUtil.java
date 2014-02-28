package com.zendesk.meetingtimes.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by barry on 28/02/2014.
 */
public class DateUtil {

    private DateUtil() {
        // Empty
    }

    public static String formatTime(Calendar calendar) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        return sdf.format(calendar.getTime());
    }

}
