package com.auto_farming.event.handler;

import com.auto_farming.event.EventManager;
import com.auto_farming.event.annotations.Event;
import com.auto_farming.event.events.basicevents.BasicEvent;
import com.auto_farming.event.events.mainevents.ScoreboardRefreshEvent;
import com.auto_farming.AutofarmingClient;
import com.auto_farming.event.EventGrouper;

public class MainEventHandler {

    @Event(ScoreboardRefreshEvent.class)
    public static void handle(ScoreboardRefreshEvent event) {

        for (Class<? extends BasicEvent > sbEvent : EventGrouper.getScoreboardEvents()) {
            try {
				EventManager.trigger(sbEvent.getDeclaredConstructor(event.getResult().getClass()).newInstance(event.getResult()));
			} catch (Exception e) {
                AutofarmingClient.LOGGER.error(e.getMessage(),e);
			}
        }
    }
}
