package com.jp.apps.weeklyreminder;

import android.content.Context;

import java.util.List;
import java.util.Map;

public interface EventDao {

    long saveEvent(Event event);

    boolean updateEvent(Event event);

    boolean develeEvent(Event event);

    List<Event> searchEvents(Map<String, String> criteria);

    long saveEventToLog(Event event, EventActions action);

    List<Event.EventLogEntry> getEventLog(Event event);
}
