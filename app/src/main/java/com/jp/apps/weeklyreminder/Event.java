package com.jp.apps.weeklyreminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

    private long id;
    private String name;
    private String description;
    private int periodicityInDays;
    private Date nextOccurrence;
    private boolean isFrozen;
    private List<EventLogEntry> eventLog = new ArrayList<>();

    public Event(long id, String name, String description, int periodicityInDays, Date nextOccurrance, boolean isFrozen) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.periodicityInDays = periodicityInDays;
        this.nextOccurrence = nextOccurrance;
        this.isFrozen = isFrozen;
    }

    public final boolean isFrozen() {
        return isFrozen;
    }

    public final void setFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public final long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final int getPeriodicityInDays() {
        return periodicityInDays;
    }

    public final void setPeriodicityInDays(int periodicityInDays) {
        this.periodicityInDays = periodicityInDays;
    }

    public final Date getNextOccurrence() {
        return nextOccurrence;
    }

    public final void setNextOccurrence(Date nextOccurrence) {
        this.nextOccurrence = nextOccurrence;
    }

    public final List<EventLogEntry> getEventLog() {
        return eventLog;
    }

    public final void setEventLog(List<EventLogEntry> eventLog) {
        this.eventLog = eventLog;
    }

    class EventLogEntry {

        private Date date;
        private EventActions action;

        public EventLogEntry(Date date, EventActions action) {
            this.date = date;
            this.action = action;
        }

        public final Date getDate() {
            return date;
        }
        public final EventActions getAction() {
            return action;
        }
    }
}
