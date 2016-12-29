package com.jp.apps.weeklyreminder;

import java.util.List;

public interface EventDao {

    long addEvent(Event event);

    boolean isNameUsed(String name);

    boolean updateEvent(Event event);

    boolean deleteEvent(Event event);

    List<Event> getAllSortedEvents();

    long saveEventToLog(Event event, Event.EventLogEntry eventLogEntry);

    List<Event.EventLogEntry> getEventLogs(Event event);

    Event.EventLogEntry getLastEventLog(Event event);
}
