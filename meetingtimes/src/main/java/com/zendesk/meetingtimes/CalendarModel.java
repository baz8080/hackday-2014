package com.zendesk.meetingtimes;

/**
 * Created by barry on 28/02/2014.
 */
public class CalendarModel {

    private long id;

    private String displayName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
