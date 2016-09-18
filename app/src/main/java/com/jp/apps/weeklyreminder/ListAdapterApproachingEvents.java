package com.jp.apps.weeklyreminder;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class ListAdapterApproachingEvents extends BaseAdapter {

    private List<Event> approachingEvents;
    private LayoutInflater mInflater;

    ListAdapterApproachingEvents(Context context, List<Event> approachingEvents) {
        this.approachingEvents = approachingEvents;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return approachingEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return approachingEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_main_approaching_events_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Event event = (Event) getItem(position);

        mViewHolder.setupApproachingEventsListItemLayout(event);

        return convertView;
    }

    private class MyViewHolder {
        LinearLayout approachingEventsListItem;

        MyViewHolder(View item) {
            approachingEventsListItem = (LinearLayout) item.findViewById(R.id.approaching_events_list_item);
        }

        private void setupApproachingEventsListItemLayout(Event event) {
            TextView name = (TextView) approachingEventsListItem.findViewById(R.id.event_list_item_name);
            name.setText(event.getName());

            ProgressBar progressBar = (ProgressBar) approachingEventsListItem.findViewById(R.id.event_list_item_progress);
            progressBar.setProgress((int) (event.getEventApproach() * 100));
        }
    }
}
