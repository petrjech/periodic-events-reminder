package com.jp.apps.weeklyreminder;

import java.util.List;

public interface EventSPI {

    void addEvent();

    void updateEvent();

    void freezeEvent();

    void deleteEvent();

    void postponeEvent();

    void skipEvent();

    void fulfillEvent();

    List<Event> searchEvents();
}
