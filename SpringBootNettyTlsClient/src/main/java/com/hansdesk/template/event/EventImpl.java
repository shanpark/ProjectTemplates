package com.hansdesk.template.event;

/**
 * Created by shanpark on 2017. 7. 21..
 */
public class EventImpl implements Event {

    private int id;
    private Object param;
    private Object param2;

    public EventImpl(int id) {
        this.id = id;
        this.param = null;
        this.param2 = null;
    }

    public EventImpl(int id, Object param) {
        this.id = id;
        this.param = param;
        this.param2 = null;
    }

    public EventImpl(int id, Object param, Object param2) {
        this.id = id;
        this.param = param;
        this.param2 = param2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public Object getParam2() {
        return param2;
    }

    public void setParam2(Object param2) {
        this.param2 = param2;
    }
}
