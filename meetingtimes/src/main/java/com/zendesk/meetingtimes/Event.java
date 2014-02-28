package com.zendesk.meetingtimes;

import java.util.Date;

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

    private Date startDateTime;

    private Date endDateTime;

    private String repeatDates;

    /**
     * final int PROJECTION_RRULE_IDX = 8;
     final int PROJECTION_HIDDEN_IDX = 9;
     final int PROJECTION_LAST_DATE_IDX = 10;
     final int PROJECTION_STATUS_IDX = 11;
     * @return
     */

    private String repeatRule;

    private boolean hidden;

    private Long lastDate;

    private int status;

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

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getRepeatRule() {
        return repeatRule;
    }

    public void setRepeatRule(String repeatRule) {
        this.repeatRule = repeatRule;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Long getLastDate() {
        return lastDate;
    }

    public void setLastDate(Long lastDate) {
        this.lastDate = lastDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRepeatDates() {
        return repeatDates;
    }

    public void setRepeatDates(String repeatDates) {
        this.repeatDates = repeatDates;
    }
}
