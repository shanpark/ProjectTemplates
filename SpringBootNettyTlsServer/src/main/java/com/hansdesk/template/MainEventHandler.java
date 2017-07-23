package com.hansdesk.template;

import com.hansdesk.template.event.*;
import com.hansdesk.template.net.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 메인 이벤트 핸들러 클래스이다. CommandLineRunner 인터페이스를 구현하면 SpringApplication.run() 함수 내에서 자동으로 호출된다.
 * run() 함수를 구현하여 eventService bean을 시작시키면, EventService 객체에서는 EventHandler 인터페이스를 구현한 이 클래스의
 * handleEvent() 함수를 발생된 Event 객체를 인자로 호출해준다. eventServive.sendEvent() 함수를 통해서 Event를 보내면 이 클래스의
 * handleEvent() 함수가 호출되는 것이다.
 *
 * Created by shanpark on 2017. 7. 21..
 */
@Component
public class MainEventHandler implements CommandLineRunner, EventHandler {
    private static Logger logger = LoggerFactory.getLogger(MainEventHandler.class);

    @Autowired
    private Server server;
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
        server.start();
    }

    private void handleStopServer() {
        server.stop();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Spring standard life-cycle annotation method.
    @PreDestroy
    private void destroy() {
        eventService.stop();
    }
}
