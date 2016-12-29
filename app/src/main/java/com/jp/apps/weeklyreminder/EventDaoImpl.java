package com.jp.apps.weeklyreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.jp.apps.weeklyreminder.DatabaseSQLiteHelper.EVENTS_COLUMNS;
import static com.jp.apps.weeklyreminder.DatabaseSQLiteHelper.EVENT_LOGS_COLUMNS;
import static com.jp.apps.weeklyreminder.DatabaseSQLiteHelper.TABLES;

public class EventDaoImpl implements EventDao {

    private final DatabaseSQLiteHelper dbHelper;

    private static final String[] eventsColumns = {
            EVENTS_COLUMNS.EVENT_ID.name(),
            EVENTS_COLUMNS.DESCRIPTION.name(),
            EVENTS_COLUMNS.IS_FROZEN.name(),
            EVENTS_COLUMNS.NAME.name(),
            EVENTS_COLUMNS.NEXT_OCCURRENCE.name(),
            EVENTS_COLUMNS.PERIODICITY.name(),
            EVENTS_COLUMNS.IS_VISIBLE_ON_WIDGET.name()
    };

    private static final String[] eventLogsColumns = {
            EVENT_LOGS_COLUMNS.EVENT_ID.name(),
            EVENT_LOGS_COLUMNS.ACTION.name(),
            EVENT_LOGS_COLUMNS.DATE.name(),
            EVENT_LOGS_COLUMNS.NOTE.name()
    };

    public EventDaoImpl(Context context) {
        this.dbHelper = DatabaseSQLiteHelper.getInstance(context);
    }

    @Override
    public long addEvent(Event event) {
        ContentValues values = getContentValues(event);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long insertedId = db.insert(TABLES.EVENTS.name(), null, values);
        db.close();
        return insertedId;
    }

    public boolean isNameUsed(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLES.EVENTS.name(), new String[]{EVENTS_COLUMNS.NAME.name()}, EVENTS_COLUMNS.NAME + "=?", new String[]{name}, null, null, null, "1");
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    @Override
    public boolean updateEvent(Event event) {
        if (event.getId() == 0L) {
            throw new IllegalArgumentException("Event id must be set.");
        }

        ContentValues values = getContentValues(event);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = EVENTS_COLUMNS.EVENT_ID.name() + " = ?";
        String[] whereArgs = {"" + event.getId()};

        int i = db.update(TABLES.EVENTS.name(), values, where, whereArgs);
        db.close();
        return i == 1;
    }

    @Override
    public boolean deleteEvent(Event event) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereEvents = EVENTS_COLUMNS.EVENT_ID.name() + " = ?";
        String whereEventLogs = EVENT_LOGS_COLUMNS.EVENT_ID.name() + " = ?";
        String[] whereArgs = {"" + event.getId()};

        int i = db.delete(TABLES.EVENTS.name(), whereEvents, whereArgs);
        db.delete(TABLES.EVENT_LOGS.name(), whereEventLogs, whereArgs);
        db.close();
        return i == 1;
    }

    @Override
    public List<Event> getAllSortedEvents() {
        List<Event> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLES.EVENTS.name(), eventsColumns, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                result.add(cursorToEvent(cursor));
            } catch (ParseException e) {
                throw new RuntimeException("Database parse error", e);
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        Collections.sort(result);
        return result;
    }

    private Event cursorToEvent(Cursor cursor) throws ParseException {
        int index = cursor.getColumnIndexOrThrow(EVENTS_COLUMNS.EVENT_ID.name());
        long id = cursor.getLong(index);

        index = cursor.getColumnIndexOrThrow(EVENTS_COLUMNS.NAME.name());
        String name = cursor.getString(index);

        index = cursor.getColumnIndexOrThrow(EVENTS_COLUMNS.DESCRIPTION.name());
        String description = cursor.getString(index);

        index = cursor.getColumnIndexOrThrow(EVENTS_COLUMNS.PERIODICITY.name());
        int periodicity = cursor.getInt(index);

        index = cursor.getColumnIndexOrThrow(EVENTS_COLUMNS.NEXT_OCCURRENCE.name());
        Date nextOccurrence = DatabaseSQLiteHelper.convertStringToDate(cursor.getString(index));

        index = cursor.getColumnIndexOrThrow(EVENTS_COLUMNS.IS_FROZEN.name());
        boolean isFrozen = cursor.getInt(index) == 1;

        index = cursor.getColumnIndexOrThrow(EVENTS_COLUMNS.IS_VISIBLE_ON_WIDGET.name());
        boolean isVisibleOnWidget = cursor.getInt(index) == 1;

        return new Event(id, name, description, periodicity, nextOccurrence, isFrozen, isVisibleOnWidget);
    }

    @Override
    public long saveEventToLog(Event event, Event.EventLogEntry eventLogEntry) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_LOGS_COLUMNS.EVENT_ID.name(), event.getId());
        values.put(EVENT_LOGS_COLUMNS.DATE.name(), DatabaseSQLiteHelper.convertDateToString(eventLogEntry.getDate()));
        values.put(EVENT_LOGS_COLUMNS.ACTION.name(), eventLogEntry.getAction().name());
        values.put(EVENT_LOGS_COLUMNS.NOTE.name(), eventLogEntry.getNote());
        return db.insert(TABLES.EVENT_LOGS.name(), null, values);
    }

    @Override
    public List<Event.EventLogEntry> getEventLogs(Event event) {
        List<Event.EventLogEntry> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLES.EVENT_LOGS.name(),
                eventLogsColumns,
                EVENT_LOGS_COLUMNS.EVENT_ID + " = ?",
                new String[] {"" + event.getId()},
                null, null,
                EVENT_LOGS_COLUMNS.DATE.name() + " DESC",
                Parameters.QUERY_EVENT_LOGS_LIMIT);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                result.add(cursorToEventLog(cursor, event));
            } catch (ParseException e) {
                throw new RuntimeException("Database parse error", e);
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return result;
    }

    @Override
    public Event.EventLogEntry getLastEventLog(Event event) {
        Event.EventLogEntry result = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLES.EVENT_LOGS.name(),
                eventLogsColumns,
                EVENT_LOGS_COLUMNS.EVENT_ID + " = ?",
                new String[] {"" + event.getId()},
                null, null,
                EVENT_LOGS_COLUMNS.DATE.name() + " DESC",
                "1");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                result = cursorToEventLog(cursor, event);
            } catch (ParseException e) {
                throw new RuntimeException("Database parse error", e);
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return result;
    }

    private Event.EventLogEntry cursorToEventLog(Cursor cursor, Event event) throws ParseException {
        int index = cursor.getColumnIndexOrThrow(EVENT_LOGS_COLUMNS.ACTION.name());
        EventActions action = EventActions.valueOf(cursor.getString(index));

        index = cursor.getColumnIndexOrThrow(EVENT_LOGS_COLUMNS.DATE.name());
        Date date = DatabaseSQLiteHelper.convertStringToDate(cursor.getString(index));

        index = cursor.getColumnIndexOrThrow(EVENT_LOGS_COLUMNS.NOTE.name());
        String note = cursor.getString(index);

        return event.new EventLogEntry(date, action, note);
    }

    @NonNull
    private ContentValues getContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EVENTS_COLUMNS.NAME.name(), event.getName());
        values.put(EVENTS_COLUMNS.DESCRIPTION.name(), event.getDescription());
        values.put(EVENTS_COLUMNS.IS_FROZEN.name(), event.isFrozen());
        values.put(EVENTS_COLUMNS.NEXT_OCCURRENCE.name(), DatabaseSQLiteHelper.convertDateToString(event.getNextOccurrence()));
        values.put(EVENTS_COLUMNS.PERIODICITY.name(), event.getPeriodicityInDays());
        values.put(EVENTS_COLUMNS.IS_VISIBLE_ON_WIDGET.name(), event.isVisibleOnWidget());
        return values;
    }
}
