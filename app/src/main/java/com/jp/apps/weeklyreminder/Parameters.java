package com.jp.apps.weeklyreminder;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Parameters {

    public final static String QUERY_EVENT_LOGS_LIMIT = "20";
    public final static String DATE_FORMAT_STRING = "d.M.yyyy";
    public final static SimpleDateFormat DATE_FORMAT;

    static {
        DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.US);
        DATE_FORMAT.setLenient(false);
    }
}
