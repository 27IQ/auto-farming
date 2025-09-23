package com.auto_farming.farminglogic;

import static com.auto_farming.input.Bindings.PAUSE_TOGGLE;

public enum PauseEvents {
    NORMAL_PAUSE("Press " + PAUSE_TOGGLE.toString() + " to resume"),
    INVENTORY_PAUSE("Force paused for inventory action");

    public final String MESSAGE;

    private PauseEvents(String messge){
        this.MESSAGE=messge;
    }
}
