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
    private Boolean isVisibleOnWidget;
    private List<EventLogEntry> eventLog = new ArrayList<>();

    private long millisecondsPerDay = 86400000L;

    public Boolean getVisibleOnWidget() {
        return isVisibleOnWidget;
    }

    public void setVisibleOnWidget(Boolean visibleOnWidget) {
        isVisibleOnWidget = visibleOnWidget;
    }

    public Event(Long id, String name, String description, Integer periodicityInDays, Date nextOccurrence, Boolean isFrozen, Boolean isVisibleOnWidget) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.periodicityInDays = periodicityInDays;
        this.nextOccurrence = nextOccurrence;
        this.isFrozen = isFrozen;
        this.isVisibleOnWidget = isVisibleOnWidget;
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

    public final float getEventApproach() {
        Date today = new Date();
        long difference = nextOccurrence.getTime() - today.getTime();
        if (difference <= 0) {
            return 1f;
        }
        long periodicityInMilliseconds = periodicityInDays * millisecondsPerDay;
        if (periodicityInMilliseconds <= difference) {
            return 0f;
        }
        float result = 1 - (((float) difference) / periodicityInMilliseconds);
        return result;
    }

    public void putExtrasToIntent(Intent intent) {
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        intent.putExtra("description", description);
        intent.putExtra("periodicityInDays", periodicityInDays);
        intent.putExtra("nextOccurrence", nextOccurrence.getTime());
        intent.putExtra("isFrozen", isFrozen);
        intent.putExtra("isVisibleOnWidget", isVisibleOnWidget);
    }

    public static Event getEventFromIntent(Intent intent) {
        Long id = intent.getLongExtra("id", 0L);
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        Integer periodicityInDays = intent.getIntExtra("periodicityInDays", 0);
        Date nextOccurrence = new Date(intent.getLongExtra("nextOccurrence", 0L));
        Boolean isFrozen = intent.getBooleanExtra("isFrozen", false);
        Boolean isVisibleOnWidget = intent.getBooleanExtra("isVisibleOnWidget", false);

        return new Event(id, name, description, periodicityInDays, nextOccurrence, isFrozen, isVisibleOnWidget);
    }

    @Override
    public int compareTo(@NonNull Event other) {
        if (this.isFrozen() && !other.isFrozen()) {
            return 1;
        }
        if (!this.isFrozen() && other.isFrozen()) {
            return -1;
        }
        if (this.isFrozen() && other.isFrozen()) {
            return this.getName().compareTo(other.getName());
        }
        return Double.compare(other.getEventApproach(), this.getEventApproach());
    }

    class EventLogEntry {

        private Date date;
        private EventActions action;
        private String note;

        public EventLogEntry(Date date, EventActions action, String note) {
            this.date = date;
            this.action = action;
            this.note = note;
        }

        public final Date getDate() {
            return date;
        }
        public final EventActions getAction() {
            return action;
        }
        public final String getNote() {
            return note;
        }
    }
}
