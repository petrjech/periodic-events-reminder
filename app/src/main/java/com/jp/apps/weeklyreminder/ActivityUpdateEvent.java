package com.jp.apps.weeklyreminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ActivityUpdateEvent extends AppCompatActivity {

    private Event event;
    private Event.EventLogEntry lastEventLog;
    private EventSPI eventSPI ;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        eventSPI = new EventSPIImpl(context);
        Intent intent = getIntent();
        event = Event.getEventFromIntent(intent);
        lastEventLog = eventSPI.getLastEventLog(this, event);

        setActivity();
    }

    private void setActivity() {
        setTitle(event.getName());

        TextView descriptionView = (TextView) findViewById(R.id.update_event_description);
        if (event.getDescription().isEmpty()) {
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.setText(event.getDescription());
        }

        TextView lastActionView = (TextView) findViewById(R.id.update_event_last_action);
        if (lastEventLog == null) {
            lastActionView.setText(context.getResources().getString(R.string.activity_update_event_no_last_action));
        } else {
            lastActionView.setText(lastEventLog.getNote() + "\n" + Parameters.DATE_FORMAT.format(lastEventLog.getDate()) + "\n" + lastEventLog.getAction().name());
        }
        // todo setup proper view

        TextView nextOccurrenceView = (TextView) findViewById(R.id.update_event_next_occurrence);
        nextOccurrenceView.setText(Parameters.DATE_FORMAT.format(event.getNextOccurrence()));

        TextView periodicityView = (TextView) findViewById(R.id.update_event_periodicity);
        periodicityView.setText(Integer.toString(event.getPeriodicityInDays()));
    }
}
