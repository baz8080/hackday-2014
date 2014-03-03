package com.zendesk.meetingtimes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zendesk.meetingtimes.util.DateUtil;
import com.zendesk.meetingtimes.util.Md5Util;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by barry on 28/02/2014.
 */
public class EventsAdapter extends ArrayAdapter<Event> {

    private List<Event> mEvents;
    private int mResource;

    public EventsAdapter(Context context, int resource, List<Event> events) {
        super(context, resource, events);
        this.mEvents = events;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(mResource, parent, false);

            Event event = mEvents.get(position);

            TextView eventName = (TextView) view.findViewById(R.id.row_event_name);
            ImageView avatarView = (ImageView) view.findViewById(R.id.row_event_avatar);
            TextView eventTime = (TextView) view.findViewById(R.id.row_event_time);

            eventName.setText(event.getTitle());

            String gravatarUrl = "http://www.gravatar.com/avatar/" + Md5Util.md5Hex(event.getOrganiserEmail());
            Picasso.with(view.getContext()).load(gravatarUrl).transform(new RoundedTransformation(40, 2)).into(avatarView);

            StringBuilder sb = new StringBuilder();

            Calendar eventStart = Calendar.getInstance();
            eventStart.setTimeZone(TimeZone.getTimeZone("UTC"));
            eventStart.setTimeInMillis(event.getStartMillisecondsUtc());
            eventStart.setTimeZone(TimeZone.getDefault());

            sb.append(DateUtil.formatTime(eventStart));

            if (event.getEndMillisecondsUtc() != 0) {
                Calendar eventEnd = Calendar.getInstance();
                eventEnd.setTimeZone(TimeZone.getTimeZone("UTC"));
                eventEnd.setTimeInMillis(event.getEndMillisecondsUtc());
                eventEnd.setTimeZone(TimeZone.getDefault());

                sb.append(" - ");
                sb.append(DateUtil.formatTime(eventEnd));
            }

            eventTime.setText(sb.toString());
        }

        return view;
    }

    public void setEvents(List<Event> events) {
        this.mEvents = events;
        notifyDataSetChanged();
    }
}
