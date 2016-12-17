package com.jp.apps.weeklyreminder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class ActivityUpdateEvent extends AppCompatActivity {

    private Event event;
    private Event.EventLogEntry lastEventLog;
    private EventDao eventDao;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        eventDao = Parameters.getEventDao();
        Intent intent = getIntent();
        event = Event.getEventFromIntent(intent);
        lastEventLog = eventDao.getLastEventLog(event);

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

    public void onAccomplishedButtonClick(View view) {
        //TODO what about frozen events?
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        final Date nextOccurrence = addDays(new Date(), event.getPeriodicityInDays());
        String newDate = Parameters.DATE_FORMAT.format(nextOccurrence);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.activity_update_event_accomplished_dialog_title);
        builder.setMessage(getString(R.string.activity_update_event_accomplished_dialog_text) + newDate + "?");
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                event.setNextOccurrence(nextOccurrence);
                accomplishEvent();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setIcon(android.R.drawable.ic_menu_help);
        builder.show();
    }

    private void accomplishEvent() {
        eventDao.updateEvent(event);
        EditText noteView = (EditText) findViewById(R.id.update_event_input_note);
        String note = noteView.getText().toString();
        Event.EventLogEntry eventLogEntry = event.new EventLogEntry(new Date(), EventActions.FULFILL, note);
        eventDao.saveEventToLog(event, eventLogEntry);
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }
}
