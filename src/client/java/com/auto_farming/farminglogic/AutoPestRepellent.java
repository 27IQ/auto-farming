package com.auto_farming.farminglogic;

import static com.auto_farming.scoreboard.RegexPattern.PEST_REPELLENT_NONE;
import static com.auto_farming.scoreboard.RegexPattern.PEST_REPELLENT_REGULAR;

import com.auto_farming.AutofarmingClient;
import com.auto_farming.event.annotations.Event;
import com.auto_farming.event.events.scoreboardevents.PestRepellentEvent;
public class AutoPestRepellent {

    @Event(PestRepellentEvent.class)
    public static void managePestRepellent(PestRepellentEvent event) {

        AutofarmingClient.LOGGER.info("suiiiii"+event.getResult().size());

        PEST_REPELLENT_NONE.getResult(event.getResult()).ifPresent((result) -> {
            if (result.found) {
                /*forcePause();
                if (isRefreshPossible()) {

                }*/
            }
        });

        PEST_REPELLENT_REGULAR.getResult(event.getResult()).ifPresent((result) -> {
            if (result.found) {
                //forcePause();
                //upgradeRepellent();
            }
        });
    }

    /*private static boolean isRefreshPossible() {

    }

    public static void upgradeRepellent() {

    }

    public static void forcePause() {

    }

    public static void resumeFarming() {

    }*/
}
