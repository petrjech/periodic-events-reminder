package com.jp.apps.weeklyreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ActivityEventHistory extends AppCompatActivity {

    private final List<Event.EventLogEntry> eventHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EventDao eventDao = Parameters.getEventDao();
        Intent intent = getIntent();
        eventHistoryList.addAll(eventDao.getEventLogs(Event.getEventFromIntent(intent)));
        ListView eventHistoryListView = (ListView) findViewById(R.id.event_history_list);
        ListAdapterEventHistory listAdapterEventHistory = new ListAdapterEventHistory(this, eventHistoryList);
        eventHistoryListView.setAdapter(listAdapterEventHistory);
    }
}
