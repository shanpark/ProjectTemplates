package com.hansdesk.template.event;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by shanpark on 2017. 7. 21..
 */
@Component
public class EventPoolImpl implements EventPool {
    private ArrayList<Event> pool = new ArrayList<>();

    @Override
    public synchronized Event getEvent(int id) {
        Event event;

        if (pool.isEmpty())
            event = new EventImpl(id);
        else {
            event = pool.remove(pool.size() - 1);
            event.setId(id);
            event.setParam(null);
            event.setParam2(null);
        }

        return event;
    }

    @Override
    public synchronized Event getEvent(int id, Object param) {
        Event event;

        if (pool.isEmpty())
            event = new EventImpl(id);
        else {
            event = pool.remove(pool.size() - 1);
            event.setId(id);
            event.setParam(param);
            event.setParam2(null);
        }

        return event;
    }

    @Override
    public synchronized Event getEvent(int id, Object param, Object param2) {
        Event event;

        if (pool.isEmpty())
            event = new EventImpl(id);
        else {
            event = pool.remove(pool.size() - 1);
            event.setId(id);
            event.setParam(param);
            event.setParam2(param2);
        }

        return event;
    }

    @Override
    public synchronized void returnEvent(Event event) {
        event.setParam(null);
        event.setParam2(null);
        pool.add(event);
    }
}
