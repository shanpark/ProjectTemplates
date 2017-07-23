package com.hansdesk.template.event;

/**
 * Created by shanpark on 2017. 7. 21..
 */
public interface Event {
    int getId();
    default void setId(int id) {}

    default Object getParam() { return null; }
    default void setParam(Object param) {}

    default Object getParam2() { return null; }
    default void setParam2(Object param2) {}
}
