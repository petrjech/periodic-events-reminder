package com.jp.apps.weeklyreminder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

public class ActivityUpdateEvent extends AppCompatActivity {
    private Event event;
    private Event.EventLogEntry lastEventLog;
    private EventDao eventDao;
    private Context context;
    private final static int MODIFY_EVENT_REQUEST = 1;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_update_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_show_settings:
                showSettings();
                return true;

            case R.id.action_modify_event:
                startModifyActivity();
                return true;

            case R.id.action_delete_event:
                showDeleteConfirmationDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSettings() {
        //TODO implement showSettings()
    }

    private void setActivity() {
        setTitle(getString(R.string.activity_update_event_title) + " " + event.getName());

        TextView descriptionView = (TextView) findViewById(R.id.update_event_description);
        TextView descriptionLabelView = (TextView) findViewById(R.id.update_event_description_label);
        if (event.getDescription().isEmpty()) {
            descriptionView.setVisibility(View.GONE);
            descriptionLabelView.setVisibility(View.GONE);
        } else {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.setText(event.getDescription());
            descriptionLabelView.setVisibility(View.VISIBLE);
        }

        TextView lastActionDateView = (TextView) findViewById(R.id.update_event_last_action_date);
        TextView lastActionNoteView = (TextView) findViewById(R.id.update_event_last_action_note);
        TextView lastActionActionView = (TextView) findViewById(R.id.update_event_last_action_action);
        if (lastEventLog == null) {
            lastActionDateView.setText(context.getResources().getString(R.string.activity_update_event_no_last_action));
            lastActionNoteView.setVisibility(View.GONE);
        } else {
            lastActionDateView.setText(Parameters.DATE_FORMAT.format(lastEventLog.getDate()));
            lastActionActionView.setText(getActionText(lastEventLog.getAction()));
            if (lastEventLog.getNote().isEmpty()) {
                lastActionNoteView.setVisibility(View.GONE);
            } else {
                lastActionNoteView.setVisibility(View.VISIBLE);
                lastActionNoteView.setText(lastEventLog.getNote());
            }
        }

        String text;
        TextView nextOccurrenceView = (TextView) findViewById(R.id.update_event_next_occurrence);
        nextOccurrenceView.setText(Parameters.DATE_FORMAT.format(event.getNextOccurrence()));

        TextView periodicityView = (TextView) findViewById(R.id.update_event_periodicity);
        periodicityView.setText(String.valueOf(event.getPeriodicityInDays()));

        TextView isActiveView = (TextView) findViewById(R.id.update_event_is_active);
        text = event.isFrozen() ? getString(R.string.no) : getString(R.string.yes);
        isActiveView.setText(text);

        TextView isWidgetVisibleView = (TextView) findViewById(R.id.update_event_is_widget_visible);
        text = event.isVisibleOnWidget() ? getString(R.string.yes) : getString(R.string.no);
        isWidgetVisibleView.setText(text);
    }

    private String getActionText(EventActions eventAction) {
        switch (eventAction) {
            case FULFILL:
                return getString(R.string.activity_event_history_action_accomplished);
            case POSTPONE:
                return getString(R.string.activity_event_history_action_postponed);
            case SKIP:
                return getString(R.string.activity_event_history_action_skipped);
        }
        return "";
    }

    public void onAccomplishedButtonClick(View view) {
        //TODO what about frozen events?
        showAccomplishConfirmationDialog();
    }

    public void onSkipButtonClick(View view) {
        showSkipConfirmationDialog();
    }

    public void onPostponeButtonClick(View view) {
        showPostponeConfirmationDialog();
    }

    public void onHistoryButtonClick(View view) {
        Intent intent = new Intent(this, ActivityEventHistory.class);
        event.putExtrasToIntent(intent);
        startActivity(intent);
    }

    public void startModifyActivity() {
        Intent intent = new Intent(this, ActivityModifyEvent.class);
        event.putExtrasToIntent(intent);
        startActivityForResult(intent, MODIFY_EVENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MODIFY_EVENT_REQUEST && resultCode == RESULT_OK) {
            event = Event.getEventFromIntent(data);
            setActivity();
        }
    }

    private void showAccomplishConfirmationDialog() {
        final Date nextOccurrence = Commons.addDays(new Date(), event.getPeriodicityInDays());
        String newDate = Parameters.DATE_FORMAT.format(nextOccurrence);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.activity_update_event_accomplished_dialog_title);
        builder.setMessage(getString(R.string.activity_update_event_accomplished_dialog_text) + newDate + "?");
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                event.setNextOccurrence(nextOccurrence);
                accomplishEvent();
                finishOk();
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

    private void showSkipConfirmationDialog() {
        final Date nextOccurrence = Commons.addDays(new Date(), event.getPeriodicityInDays());
        String newDate = Parameters.DATE_FORMAT.format(nextOccurrence);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.activity_update_event_skip_dialog_title);
        builder.setMessage(getString(R.string.activity_update_event_skip_dialog_text) + newDate + "?");
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                event.setNextOccurrence(nextOccurrence);
                skipEvent();
                finishOk();
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

    private void showPostponeConfirmationDialog() {
        EditText postponeDaysView = (EditText) findViewById(R.id.update_event_input_days);
        int postponeDays = 0;
        try {
            String postponeDaysString = postponeDaysView.getText().toString();
            postponeDays = Integer.valueOf(postponeDaysString);
        } catch (NumberFormatException ignore) {}
        if (postponeDays < 1 || postponeDays > 9999) {
            Commons.showErrorToast(context, getString(R.string.activity_update_event_error_postpone_days));
        }
        final Date nextOccurrence = Commons.addDays(event.getNextOccurrence(), postponeDays);
        String newDate = Parameters.DATE_FORMAT.format(nextOccurrence);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.activity_update_event_postpone_dialog_title);
        builder.setMessage(getString(R.string.activity_update_event_postpone_dialog_text) + newDate + "?");
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                event.setNextOccurrence(nextOccurrence);
                postponeEvent();
                finishOk();
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

    private void skipEvent() {
        eventDao.updateEvent(event);
        EditText noteView = (EditText) findViewById(R.id.update_event_input_note);
        String note = noteView.getText().toString();
        Event.EventLogEntry eventLogEntry = event.new EventLogEntry(new Date(), EventActions.SKIP, note);
        eventDao.saveEventToLog(event, eventLogEntry);
    }

    private void postponeEvent() {
        eventDao.updateEvent(event);
        EditText noteView = (EditText) findViewById(R.id.update_event_input_note);
        String note = noteView.getText().toString();
        Event.EventLogEntry eventLogEntry = event.new EventLogEntry(new Date(), EventActions.POSTPONE, note);
        eventDao.saveEventToLog(event, eventLogEntry);
    }

    private void finishOk() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.activity_update_event_delete_dialog_title);
        builder.setMessage(R.string.activity_update_event_delete_dialog_text);
        builder.setPositiveButton(R.string.activity_update_event_delete_delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!eventDao.deleteEvent(event)) {
                    throw new RuntimeException("Error deleting event");
                }
                finishOk();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setIcon(android.R.drawable.ic_menu_delete );
        builder.show();
    }
}
