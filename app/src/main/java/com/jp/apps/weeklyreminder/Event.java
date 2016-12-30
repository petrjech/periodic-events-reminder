package com.jp.apps.weeklyreminder;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Date;

public class Event implements Comparable<Event> {

    private Long id;
    private String name;
    private String description;
    private Integer periodicityInDays;
    private Date nextOccurrence;
    private Boolean isFrozen;
    private Boolean isVisibleOnWidget;

    private static final long MILLISECONDS_PER_DAY = 86400000L;

    public Boolean isVisibleOnWidget() {
        return isVisibleOnWidget;
    }

    public void setVisibleOnWidget(Boolean visibleOnWidget) {
        isVisibleOnWidget = visibleOnWidget;
    }

    public Event() {
        this(null, null, null, null, null, null, null);
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

    public final float getEventApproach() {
        Date today = new Date();
        long difference = nextOccurrence.getTime() - today.getTime();
        if (difference <= 0) {
            return 1f;
        }
        long periodicityInMilliseconds = periodicityInDays * MILLISECONDS_PER_DAY;
        if (periodicityInMilliseconds <= difference) {
            return 0f;
        }
        return 1 - (((float) difference) / periodicityInMilliseconds);
    }

    public void putExtrasToIntent(Intent intent) {
        if (id != null) {
            intent.putExtra("id", id);
        }
        if (id != null) {
            intent.putExtra("name", name);
        }
        if (id != null) {
            intent.putExtra("description", description);
        }
        if (id != null) {
            intent.putExtra("periodicityInDays", periodicityInDays);
        }
        if (id != null) {
            intent.putExtra("nextOccurrence", nextOccurrence.getTime());
        }
        if (id != null) {
            intent.putExtra("isFrozen", isFrozen);
        }
        if (id != null) {
            intent.putExtra("isVisibleOnWidget", isVisibleOnWidget);
        }
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
