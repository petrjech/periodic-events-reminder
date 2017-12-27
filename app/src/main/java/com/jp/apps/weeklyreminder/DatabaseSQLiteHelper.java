package com.jp.apps.weeklyreminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weekly_reminder.db";
    private static final int DATABASE_VERSION = 1;

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public enum TABLES {
        EVENTS, EVENT_LOGS
    }

    public enum EVENTS_COLUMNS {
        EVENT_ID, NAME, DESCRIPTION, PERIODICITY, NEXT_OCCURRENCE, IS_FROZEN, IS_VISIBLE_ON_WIDGET
    }

    public enum EVENT_LOGS_COLUMNS {
        EVENT_ID, DATE, ACTION, NOTE
    }

    private static DatabaseSQLiteHelper instance;

    private DatabaseSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseSQLiteHelper getInstance(Context context){
        if (instance == null){
            instance = new DatabaseSQLiteHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(createTableEvents());
        database.execSQL(createTableEventLogs());
        database.execSQL(createEventLogsIndex());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseSQLiteHelper.class.getName(), "Version 1 - upgrading of the database isn't implemented");
        throw new UnsupportedOperationException("Version 1 - upgrading of the database isn't implemented");
    }

    private static String createTableEvents(){
        return "CREATE TABLE " + TABLES.EVENTS.name() +"("
                + EVENTS_COLUMNS.EVENT_ID.name()      + " INTEGER PRIMARY KEY ASC,"
                + EVENTS_COLUMNS.NAME.name()          + " TEXT NOT NULL,"
                + EVENTS_COLUMNS.DESCRIPTION.name()   + " TEXT NOT NULL,"
                + EVENTS_COLUMNS.PERIODICITY.name()   + " INTEGER NOT NULL,"
                + EVENTS_COLUMNS.NEXT_OCCURRENCE.name() + " TEXT NOT NULL,"
                + EVENTS_COLUMNS.IS_FROZEN.name()     + " INTEGER NOT NULL,"
                + EVENTS_COLUMNS.IS_VISIBLE_ON_WIDGET.name() + " TEXT"
                + ");";
    }

    private static String createTableEventLogs(){
        return "CREATE TABLE " + TABLES.EVENT_LOGS.name() + "("
                + EVENT_LOGS_COLUMNS.EVENT_ID.name()  + " INTEGER NOT NULL,"
                + EVENT_LOGS_COLUMNS.DATE.name()      + " TEXT NOT NULL,"
                + EVENT_LOGS_COLUMNS.ACTION.name() + " TEXT NOT NULL,"
                + EVENT_LOGS_COLUMNS.NOTE.name() + " TEXT,"
                + "FOREIGN KEY(" + EVENT_LOGS_COLUMNS.EVENT_ID.name() + ") REFERENCES EVENTS(" + EVENTS_COLUMNS.EVENT_ID.name() + ")"
                + ");";
    }

    private static String createEventLogsIndex(){
        return "CREATE INDEX EVENT_LOGS_IDX ON " + TABLES.EVENT_LOGS + "("
                + EVENT_LOGS_COLUMNS.EVENT_ID.name()  + ", " + EVENT_LOGS_COLUMNS.DATE.name() + " DESC);";
    }

    public static String convertDateToString(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date convertStringToDate(String date) throws ParseException {
        return DATE_FORMAT.parse(date);
    }
}
