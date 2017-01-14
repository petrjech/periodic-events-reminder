package com.jp.apps.weeklyreminder;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Parameters {

    public final static String QUERY_EVENT_LOGS_LIMIT = "20";
    public final static String DATE_FORMAT_STRING = "d.M.yyyy";
    public final static SimpleDateFormat DATE_FORMAT;
    public final static int MAX_EVENTS_ON_APP_WIDGET = 3;
    public final static float EVENT_APPROACHING_THRESHOLD1 = 0.66f;
    public final static float EVENT_APPROACHING_THRESHOLD2 = 0.94f;


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
