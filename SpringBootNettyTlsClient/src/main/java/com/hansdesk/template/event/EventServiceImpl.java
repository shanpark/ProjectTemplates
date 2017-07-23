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
        Thread.currentThread().setName("evt_handlr");
        logger.info("EventService starts.");

        Event event = null;
        boolean idle = false;

        // custom event queue loop
        while (true) {
            try {
                if (idle)
                    event = eventQueue.poll(1, TimeUnit.SECONDS); // wait 1 second.
                else
                    event = eventQueue.poll(); // no wait.

                if (event == null) {
                    idle = true;
                    event = eventPool.getEvent(EventType.ID_IDLE);
                }
                else {
                    idle = false;
                }

                eventHandler.handleEvent(event);

                if (event.getId() == EventType.ID_SHUTDOWN)
                    break;
            } catch (Exception e) {
                logger.error("An exception occurred in AlbatrossMQ.eventLoop().", e);
            }

            if (event != null) {
                eventPool.returnEvent(event);
                event = null;
            }
        }

        logger.info("EventService ends.");
    }
}
