package com.zendesk.meetingtimes;

import android.database.Cursor;

/**
 * Created by barry on 27/02/2014.
 */
public class Event {

    private String organiserEmail;

    private String title;

    private String location;

    private String description;

    private Long startMillisecondsUtc;

    private Long endMillisecondsUtc;

    private String startTimezone;

    private String endTimeZone;

    public static Event fromCursor(Cursor cursor) {
        Event event = new Event();

        // TODO populate

        return event;
    }

    public String getOrganiserEmail() {
        return organiserEmail;
    }

    public void setOrganiserEmail(String organiserEmail) {
        this.organiserEmail = organiserEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getStartMillisecondsUtc() {
        return startMillisecondsUtc;
    }

    public void setStartMillisecondsUtc(Long startMillisecondsUtc) {
        this.startMillisecondsUtc = startMillisecondsUtc;
    }

    public Long getEndMillisecondsUtc() {
        return endMillisecondsUtc;
    }

    public void setEndMillisecondsUtc(Long endMillisecondsUtc) {
        this.endMillisecondsUtc = endMillisecondsUtc;
    }

    public String getStartTimezone() {
        return startTimezone;
    }

    public void setStartTimezone(String startTimezone) {
        this.startTimezone = startTimezone;
    }

    public String getEndTimeZone() {
        return endTimeZone;
    }

    public void setEndTimeZone(String endTimeZone) {
        this.endTimeZone = endTimeZone;
    }
}
