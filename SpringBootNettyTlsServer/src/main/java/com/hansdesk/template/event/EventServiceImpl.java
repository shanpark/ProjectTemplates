package com.hansdesk.template.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by shanpark on 2017. 7. 21..
 */
@Component
public class EventServiceImpl implements EventService, Runnable {
    private static Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private EventHandler eventHandler;
    @Autowired
    private EventPool eventPool;

    private LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>(); // thread-safe

    @Override
    public void start() {
        new Thread(this).start();
    }

    @Override
    public void stop() {
        sendEvent(eventPool.getEvent(EventType.ID_SHUTDOWN));
    }

    @Override
    public void sendEvent(Event event) {
        eventQueue.add(event);
    }

    @Override
    public void run() {
        logger.info("EventService starts.");
        Thread.currentThread().setName("evt_handlr");

        Event event = null;
        boolean idle = false;

        // custom event queue loop
        do {
            try {
                if (idle)
                    event = eventQueue.poll(1000, TimeUnit.MILLISECONDS); // wait 1 second.
                else
                    event = eventQueue.poll(); // no wait

                if (event != null) {
                    idle = false;
                }
                else {
                    event = eventPool.getEvent(EventType.ID_IDLE);
                    idle = true;
                }

                eventHandler.handleEvent(event);
            } catch (Exception e) {
                logger.error("An exception occurred in AlbatrossMQ.eventLoop().", e);
                if (event == null)
                    event = eventPool.getEvent(EventType.ID_IDLE);
            }
        } while (event.getId() != EventType.ID_SHUTDOWN);

        logger.info("EventService ends.");
    }
}
