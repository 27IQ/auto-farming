package com.auto_farming.event.events.mainevents;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.auto_farming.event.events.basicevents.BasicEvent;
import com.auto_farming.farminglogic.disrupt.Disrupt;

public class AfterDisruptEvent extends BasicEvent {

    private Set<Disrupt> executedDisrupts;

    public AfterDisruptEvent(HashSet<Disrupt> executedDisrupts) {
        super();
        this.executedDisrupts = executedDisrupts;
    }

    public Set<Disrupt> getExecutedDisrupts() {
        return Collections.unmodifiableSet(executedDisrupts);
    }
}
