package com.hansdesk.template.event;

/**
 * Created by shanpark on 2017. 7. 21..
 */
public interface EventService {
    void start();
    void stop();

    void sendEvent(Event event);
}
