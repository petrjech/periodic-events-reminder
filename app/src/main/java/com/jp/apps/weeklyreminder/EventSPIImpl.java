package com.jp.apps.weeklyreminder;

import android.content.Context;

import java.util.Collections;
import java.util.List;

public class EventSPIImpl implements EventSPI {

    private EventDao eventDao;

    public EventSPIImpl(Context context) {
        eventDao = new EventDaoImpl(context);
    }

    @Override
    public void addEvent() {

    }

    @Override
    public void updateEvent() {

    }

    @Override
    public void freezeEvent() {

    }

    @Override
    public void deleteEvent() {

    }

    @Override
    public void postponeEvent() {

    }

    @Override
    public void skipEvent() {

    }

    @Override
    public void fulfillEvent() {

    }

    @Override
    public List<Event> getAllSortedEvents(Context context) {
        List<Event> events = eventDao.getAllEvents();
        Collections.sort(events);
        return events;
    }

    @Override
    public void getEventLogs(Context context, Event event) {
        event.setEventLog(eventDao.getEventLogs(event));
    }
}
