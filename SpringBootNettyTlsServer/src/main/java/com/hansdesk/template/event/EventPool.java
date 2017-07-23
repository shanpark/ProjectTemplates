package com.hansdesk.template.event;

/**
 * Created by shanpark on 2017. 7. 21..
 */
public interface EventPool {
    Event getEvent(int id);
    Event getEvent(int id, Object param);
    Event getEvent(int id, Object param, Object param2);

    void returnEvent(Event event);
}
