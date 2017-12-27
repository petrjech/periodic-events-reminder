package com.jp.apps.weeklyreminder;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import layout.EventAppWidget;

public class ActivityMain extends AppCompatActivity {
    //TODO introduce event categories and category filtering?

    private final static int ADD_EVENT_REQUEST = 0;
    private final static int UPDATE_EVENT_REQUEST = 1;

    private final List<Event> approachingEventsList = new ArrayList<>();
    private ListAdapterApproachingEvents listAdapterApproachingEvents;
    private EventDao eventDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialize(this);

        eventDao = Parameters.getEventDao();
        approachingEventsList.addAll(eventDao.getAllSortedEvents());
        ListView approachingEventsListView = (ListView) findViewById(R.id.approaching_events_list);
        listAdapterApproachingEvents = new ListAdapterApproachingEvents(this, approachingEventsList);
        approachingEventsListView.setAdapter(listAdapterApproachingEvents);

        approachingEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                openEventActivity(event);
            }
        });
    }

    private void updateWidget(Context context) {
        Intent intent = new Intent(context, EventAppWidget.class);
        intent.setAction(EventAppWidget.UPDATE_WIDGET_ACTION);
        AppWidgetManager appManager = AppWidgetManager.getInstance(getApplication());
        int[] ids = appManager.getAppWidgetIds(new ComponentName(getApplication(), EventAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    private void initialize(Context context) {
        Parameters.setEventDao(new EventDaoImpl(context));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_show_settings:
                showSettings();
                return true;

            case R.id.action_add_event:
                addEvent();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addEvent() {
        Intent intent = new Intent(this, ActivityAddEvent.class);
        startActivityForResult(intent, ADD_EVENT_REQUEST);
    }

    private void showSettings() {
        //TODO implement showSettings()
    }

    private void openEventActivity(Event event) {
        Intent intent = new Intent(this, ActivityUpdateEvent.class);
        event.putExtrasToIntent(intent);
        startActivityForResult(intent, UPDATE_EVENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == ADD_EVENT_REQUEST || requestCode == UPDATE_EVENT_REQUEST) && resultCode == RESULT_OK) {
            approachingEventsList.clear();
            approachingEventsList.addAll(eventDao.getAllSortedEvents());
            listAdapterApproachingEvents.notifyDataSetChanged();
            updateWidget(this);
        }
    }
}