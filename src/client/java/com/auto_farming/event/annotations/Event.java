package com.auto_farming.event.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.auto_farming.event.events.basicevents.BasicEvent;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Event {
    Class<? extends BasicEvent> value();
}
