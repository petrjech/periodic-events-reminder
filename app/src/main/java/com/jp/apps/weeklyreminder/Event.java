package com.jp.apps.weeklyreminder;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Comparable<Event> {

    private Long id;
    private String name;
    private String description;
    private Integer periodicityInDays;
    private Date nextOccurrence;
    private Boolean isFrozen;
    private List<EventLogEntry> eventLog = new ArrayList<>();

    public Event(Long id, String name, String description, Integer periodicityInDays, Date nextOccurrance, Boolean isFrozen) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.periodicityInDays = periodicityInDays;
        this.nextOccurrence = nextOccurrance;
        this.isFrozen = isFrozen;
    }

    public final Boolean isFrozen() {
        return isFrozen;
    }

    public final void setFrozen(Boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public final Long getId() {
        return id;
    }

    public final void setId(Long id) {
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

    public final Integer getPeriodicityInDays() {
        return periodicityInDays;
    }

    public final void setPeriodicityInDays(Integer periodicityInDays) {
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

    public final double getEventApproach() {
        double result;
        Date today = new Date();
        long difference = nextOccurrence.getTime() - today.getTime();
        if (difference <= 0) {
            return 1;
        }
        long periodicityInMilliseconds = periodicityInDays * 24 * 60 * 60 * 1000;
        if (periodicityInMilliseconds <= difference) {
            return 0;
        }
        return  ((double) periodicityInMilliseconds - difference) / periodicityInMilliseconds;
    }

    public void putExtrasToIntent(Intent intent) {
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("description", description);
        intent.putExtra("periodicityInDays", periodicityInDays);
        intent.putExtra("nextOccurrence", nextOccurrence.getTime());
        intent.putExtra("isFrozen", isFrozen);
    }

    public static Event getEventFromIntent(Intent intent) {
        Long id = intent.getLongExtra("id", 0L);
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        Integer periodicityInDays = intent.getIntExtra("periodicityInDays", 0);
        Date nextOccurrence = new Date(intent.getLongExtra("nextOccurrence", 0L));
        Boolean isFrozen = intent.getBooleanExtra("isFrozen", false);

        return new Event(id, name, description, periodicityInDays, nextOccurrence, isFrozen);
    }

    @Override
    public int compareTo(@NonNull Event other) {
        return Double.compare(this.getEventApproach(), other.getEventApproach());
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
