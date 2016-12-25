package com.jp.apps.weeklyreminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ListAdapterEventHistory extends BaseAdapter {

    private List<Event.EventLogEntry> eventHistoryList;
    private LayoutInflater mInflater;
    private Context context;

    ListAdapterEventHistory(Context context, List<Event.EventLogEntry> eventHistoryList) {
        this.eventHistoryList = eventHistoryList;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return eventHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_event_history_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Event.EventLogEntry eventLogEntry = (Event.EventLogEntry) getItem(position);

        mViewHolder.setupEventHistoryListItemView(eventLogEntry);

        return convertView;
    }

    private class MyViewHolder {
        LinearLayout eventHistoryListItemView;

        MyViewHolder(View item) {
            eventHistoryListItemView = (LinearLayout) item.findViewById(R.id.events_history_list_item);
        }

        private void setupEventHistoryListItemView(Event.EventLogEntry eventLogEntry) {
            TextView dateView = (TextView) eventHistoryListItemView.findViewById(R.id.events_history_list_item_date);
            TextView actionView = (TextView) eventHistoryListItemView.findViewById(R.id.events_history_list_item_action);
            TextView noteVIew = (TextView) eventHistoryListItemView.findViewById(R.id.events_history_list_item_note);
            View colorView = eventHistoryListItemView.findViewById(R.id.events_history_list_item_color);

            dateView.setText(Parameters.DATE_FORMAT.format(eventLogEntry.getDate()));
            actionView.setText(Commons.getActionText(context, eventLogEntry.getAction()));
            colorView.setBackgroundColor(Commons.getActionColor(context, eventLogEntry.getAction()));

            if (eventLogEntry.getNote().isEmpty()) {
                noteVIew.setVisibility(View.GONE);
            } else {
                noteVIew.setVisibility(View.VISIBLE);
                noteVIew.setText(eventLogEntry.getNote());
            }
        }
    }
}
