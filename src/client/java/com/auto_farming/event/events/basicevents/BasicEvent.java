package com.auto_farming.event.events.basicevents;

public abstract class BasicEvent {
    protected String name;

    public BasicEvent() {
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }
}
