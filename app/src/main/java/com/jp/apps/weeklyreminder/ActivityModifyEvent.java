package com.jp.apps.weeklyreminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Date;

public class ActivityModifyEvent extends AppCompatActivity {

    private Event event;
    private EventDao eventDao;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        eventDao = Parameters.getEventDao();
        Intent intent = getIntent();
        event = Event.getEventFromIntent(intent);

        setActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_modify_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_show_settings:
                showSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSettings() {
        //TODO implement showSettings()
    }

    public void onSubmitButtonClick(@SuppressWarnings("UnusedParameters") View view) {

        if (!setUpEvent()) {
            return;
        }

        if (!eventDao.updateEvent(event)) {
            throw new RuntimeException("Update event error");
        }

        Intent intent = new Intent();
        event.putExtrasToIntent(intent);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setActivity() {
        setTitle(getString(R.string.activity_modify_event_title) + " " + event.getName());

        EditText nameInputView = (EditText) findViewById(R.id.modify_event_input_name);
        nameInputView.setText(event.getName());

        EditText descriptionInputView = (EditText) findViewById(R.id.modify_event_input_description);
        descriptionInputView.setText(event.getDescription());

        EditText periodicityInputView = (EditText) findViewById(R.id.modify_event_input_periodicity);
        periodicityInputView.setText(String.valueOf(event.getPeriodicityInDays()));

        EditText nextOccurrenceInputView = (EditText) findViewById(R.id.modify_event_input_occurrence);
        nextOccurrenceInputView.setText(Parameters.DATE_FORMAT.format(event.getNextOccurrence()));

        CheckBox isActiveCheckBox = (CheckBox) findViewById(R.id.modify_event_checkbox_active);
        isActiveCheckBox.setChecked(!event.isFrozen());

        CheckBox isVisibleCheckBox = (CheckBox) findViewById(R.id.modify_event_checkbox_visible);
        isVisibleCheckBox.setChecked(event.isVisibleOnWidget());
    }

    private boolean setUpEvent() {
        return setUpName()
                && setUpDescription()
                && setUpPeriodicity()
                && setUpNextOccurrence()
                && setUpFrozen()
                && setUpVisibleOnWidget();
    }

    private boolean setUpName() {
        EditText editTextName = (EditText) findViewById(R.id.modify_event_input_name);
        String name = editTextName.getText().toString();

        if (name.equals(event.getName())) {
            return true;
        }

        if (name.isEmpty()) {
            Commons.showErrorToast(context, getString(R.string.activity_modify_event_error_no_name));
            return false;
        }
        if (eventDao.isNameUsed(name)) {
            Commons.showErrorToast(context, getString(R.string.activity_modify_event_error_name_exists));
            return false;
        }
        event.setName(name);
        return true;
    }

    private boolean setUpDescription() {
        EditText editTextDescription = (EditText) findViewById(R.id.modify_event_input_description);
        String description = editTextDescription.getText().toString();
        event.setDescription(description);
        return true;
    }

    private boolean setUpPeriodicity() {
        EditText editTextPeriodicity = (EditText) findViewById(R.id.modify_event_input_periodicity);
        String periodicityString = editTextPeriodicity.getText().toString();

        Integer periodicity = null;
        boolean isPeriodicityValid = true;
        if (periodicityString.isEmpty()) {
            isPeriodicityValid = false;
        }
        if (isPeriodicityValid) {
            try {
                periodicity = Integer.valueOf(periodicityString);
            } catch (NumberFormatException e) {
                isPeriodicityValid = false;
            }
        }
        if (!isPeriodicityValid || periodicity < 1 || periodicity > 9999) {
            Commons.showErrorToast(context, getString(R.string.activity_modify_event_error_periodicity));
            return false;
        }
        event.setPeriodicityInDays(periodicity);
        return true;
    }

    private boolean setUpNextOccurrence() {
        EditText editTextNextOccurrence = (EditText) findViewById(R.id.modify_event_input_occurrence);
        String nextOccurrenceString = editTextNextOccurrence.getText().toString();
        Date nextOccurrence;
        try {
            nextOccurrence = Parameters.DATE_FORMAT.parse(nextOccurrenceString);
        } catch (ParseException e) {
            Commons.showErrorToast(context, getString(R.string.activity_modify_event_error_date_format) + Parameters.DATE_FORMAT_STRING);
            return false;
        }
        if (nextOccurrence.compareTo(new Date()) < 1) {
            Commons.showErrorToast(context, getString(R.string.activity_modify_event_error_date));
            return false;
        }
        event.setNextOccurrence(nextOccurrence);
        return true;
    }

    private boolean setUpFrozen() {
        CheckBox checkboxActive = (CheckBox) findViewById(R.id.modify_event_checkbox_active);
        event.setFrozen(!checkboxActive.isChecked());
        return true;
    }

    private boolean setUpVisibleOnWidget() {
        CheckBox checkboxVisible = (CheckBox) findViewById(R.id.modify_event_checkbox_visible);
        event.setVisibleOnWidget(checkboxVisible.isChecked());
        return true;
    }
}





