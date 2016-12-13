package com.jp.apps.weeklyreminder;

import android.content.Context;

import java.util.List;

public interface EventSPI {

    void addEvent(Event event);

    boolean isEventNameUsed(String name);

    void updateEvent();

    void freezeEvent();

    void deleteEvent();

    void postponeEvent();

    void skipEvent();

    void fulfillEvent();

    List<Event> getAllSortedEvents(Context context);

    void getEventLogs(Context context, Event event);

    Event.EventLogEntry getLastEventLog(Context context, Event event);
}
