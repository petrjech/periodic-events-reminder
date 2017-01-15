package com.jp.apps.weeklyreminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class ActivityAddEvent extends AppCompatActivity {

    private Event event = new Event();
    private EventDao eventDao;
    private Context context;
    private TextView firstOccurrenceView;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        eventDao = Parameters.getEventDao();

        calendar.setTime(new Date());
        firstOccurrenceView = (TextView) findViewById(R.id.add_event_input_occurrence);
        firstOccurrenceView.setText(Parameters.DATE_FORMAT.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_add_event_menu, menu);
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

        if (!setEvent()) {
            return;
        }

        eventDao.addEvent(event);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ActivityAddEvent activity = (ActivityAddEvent) getActivity();
            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
                    activity.calendar.get(Calendar.YEAR),
                    activity.calendar.get(Calendar.MONTH),
                    activity.calendar.get(Calendar.DAY_OF_MONTH));
            DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMinDate(activity.calendar.getTimeInMillis());
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ActivityAddEvent activity = (ActivityAddEvent) getActivity();
            activity.calendar.set(year, month, day);
            activity.firstOccurrenceView.setText(Parameters.DATE_FORMAT.format(activity.calendar.getTime()));
        }
    }

    public void showOccurrenceDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private boolean setEvent() {
        return setName()
                && setDescription()
                && setPeriodicity()
                && setNextOccurrence()
                && setIsFrozen()
                && setVisibleOnWidget();
    }

    private boolean setName() {
        TextInputEditText textInputEditTextName = (TextInputEditText) findViewById(R.id.add_event_input_name);
        String name = textInputEditTextName.getText().toString();
        if (name.isEmpty()) {
            Commons.showErrorToast(context, getString(R.string.activity_add_event_error_no_name));
            return false;
        }
        if (eventDao.isNameUsed(name)) {
            Commons.showErrorToast(context, getString(R.string.activity_add_event_error_name_exists));
            return false;
        }
        event.setName(name);
        return true;
    }

    private boolean setDescription() {
        TextInputEditText textInputEditTextDescription = (TextInputEditText) findViewById(R.id.add_event_input_description);
        String description = textInputEditTextDescription.getText().toString();
        event.setDescription(description);
        return true;
    }

    private boolean setPeriodicity() {
        TextInputEditText textInputEditTextPeriodicity = (TextInputEditText) findViewById(R.id.add_event_input_periodicity);
        String periodicityString = textInputEditTextPeriodicity.getText().toString();

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
            Commons.showErrorToast(context, getString(R.string.activity_add_event_error_periodicity));
            return false;
        }
        event.setPeriodicityInDays(periodicity);
        return true;
    }

    private boolean setNextOccurrence() {
        event.setNextOccurrence(calendar.getTime());
        return true;
    }

    private boolean setIsFrozen() {
        CheckBox checkboxActive = (CheckBox) findViewById(R.id.add_event_checkbox_active);
        event.setFrozen(!checkboxActive.isChecked());
        return true;
    }

    private boolean setVisibleOnWidget() {
        CheckBox checkboxVisible = (CheckBox) findViewById(R.id.add_event_checkbox_visible);
        event.setVisibleOnWidget(checkboxVisible.isChecked());
        return true;
    }
}
