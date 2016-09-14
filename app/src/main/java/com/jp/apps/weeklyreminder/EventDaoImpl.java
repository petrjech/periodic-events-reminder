package com.jp.apps.weeklyreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.Map;

public class EventDaoImpl implements EventDao {

    private DatabaseSQLiteHelper dbHelper;

    public EventDaoImpl(Context context) {
        this.dbHelper = DatabaseSQLiteHelper.getInstance(context);
    }

    @Override
    public long saveEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteHelper.EVENTS_COLUMNS.NAME.name(), event.getName());
        values.put(DatabaseSQLiteHelper.EVENTS_COLUMNS.DESCRIPTION.name(), event.getDescription());
        values.put(DatabaseSQLiteHelper.EVENTS_COLUMNS.IS_FROZEN.name(), event.isFrozen());
        values.put(DatabaseSQLiteHelper.EVENTS_COLUMNS.NEXT_OCCURRENCE.name(), DatabaseSQLiteHelper.convertDateToString(event.getNextOccurrence()));
        values.put(DatabaseSQLiteHelper.EVENTS_COLUMNS.PERIODICITY.name(), event.getPeriodicityInDays());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long insertedId = db.insert("EVENTS", null, values);
        db.close();

        return insertedId;
    }

    @Override
    public boolean updateEvent(Event event) {
        return false;
    }

    @Override
    public boolean develeEvent(Event event) {
        return false;
    }

    @Override
    public List<Event> searchEvents(Map<String, String> criteria) {
        return null;
    }

    @Override
    public long saveEventToLog(Event event, EventActions action) {
        return 0;
    }

    @Override
    public List<Event.EventLogEntry> getEventLog(Event event) {
        return null;
    }
}
