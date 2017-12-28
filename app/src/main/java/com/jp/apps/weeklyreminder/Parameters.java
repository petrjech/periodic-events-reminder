package com.jp.apps.weeklyreminder;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Parameters {

    public final static String QUERY_EVENT_LOGS_LIMIT = "20";
    public final static String DATE_FORMAT_STRING = "d.M.yyyy HH:mm";
    public final static SimpleDateFormat DATE_FORMAT;
    public final static float EVENT_APPROACHING_THRESHOLD1 = 0.66f;
    public final static float EVENT_APPROACHING_THRESHOLD2 = 0.94f;
    public final static String PREFERENCES = "WeeklyReminderPreferences";

    public final static int WIDGET_LINE_MAX_LENGTH = 15;
    public final static int WIDGET_MAX_LINES = 6;


    private static EventDao eventDao;

    static {
        DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.US);
        DATE_FORMAT.setLenient(false);
    }

    public static void setEventDao(EventDao aEventDao) {
        eventDao = aEventDao;
    }

    public static EventDao getEventDao() {
        return eventDao;
    }
}
