package com.jp.apps.weeklyreminder;

import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Commons {

    static void showErrorToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }

    static String getActionText(Context context, EventActions eventAction) {
        switch (eventAction) {
            case FULFILL:
                return context.getString(R.string.activity_event_history_action_accomplished);
            case POSTPONE:
                return context.getString(R.string.activity_event_history_action_postponed);
            case SKIP:
                return context.getString(R.string.activity_event_history_action_skipped);
        }
        return "";
    }

    static int getActionColor(Context context, EventActions eventAction) {
        switch (eventAction) {
            case FULFILL:
                return context.getResources().getColor(R.color.colorEventHistoryActionAccomplished);
            case POSTPONE:
                return context.getResources().getColor(R.color.colorEventHistoryActionPostponed);
            case SKIP:
                return context.getResources().getColor(R.color.colorEventHistoryActionSkipped);
        }
        return 0;
    }

    static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }
}
