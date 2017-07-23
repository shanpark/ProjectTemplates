package com.hansdesk.template;

import com.hansdesk.template.event.*;
import com.hansdesk.template.net.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * Created by shanpark on 2017. 7. 21..
 */
@Component
public class MainEventHandler implements CommandLineRunner, EventHandler {
    private static Logger logger = LoggerFactory.getLogger(MainEventHandler.class);

    @Autowired
    private Client client;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventPool eventPool;

    ///////////////////////////////////////////////////////////////////////////
    // CommandLineRunner interface implements
    @Override
    public void run(String... args) throws Exception {
        eventService.start();
        eventService.sendEvent(eventPool.getEvent(EventType.ID_START));
    }

    ///////////////////////////////////////////////////////////////////////////
    // EventHandler interface implements
    @Override
    public void handleEvent(Event event) {
        switch (event.getId()) {
            case EventType.ID_IDLE:
                break;
            case EventType.ID_START:
                handleStartServer();
                break;
            case EventType.ID_SHUTDOWN:
                handleStopServer();
                break;
        }

        eventPool.returnEvent(event);
    }

    private void handleStartServer() {
        client.start();
    }

    private void handleStopServer() {
        client.stop();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spring standard life-cycle annotation method.
    @PreDestroy
    private void destroy() {
        eventService.stop();
    }
}
