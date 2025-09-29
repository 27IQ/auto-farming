package com.auto_farming.farminglogic.autofarmextensions;

import static com.auto_farming.scoreboard.RegexPattern.PEST_ALIVE_PATTERN;

import java.util.Optional;

import com.auto_farming.event.annotations.Event;
import com.auto_farming.event.events.scoreboardevents.PestLimitEvent;
import com.auto_farming.farminglogic.AutoFarm;
import com.auto_farming.farminglogic.AutoFarmHolder;
import com.auto_farming.farminglogic.disrupt.Disrupt;
import com.auto_farming.scoreboard.RegexResult;

public class PestDetection {

    @Event(PestLimitEvent.class)
    public static void checkForPestLimit(PestLimitEvent event) {
        Optional<AutoFarm> farm = AutoFarmHolder.get();

        if (farm.isEmpty())
            return;

        RegexResult result = event.getResult().get(PEST_ALIVE_PATTERN);

        if (!result.found)
            return;

        int pestNumber = Integer.valueOf(result.matcher.group(1));

        if (pestNumber >= farm.get().getMaximumPestNumber())
            farm.get().queueDisruptIfAbesent(Disrupt.PEST_LIMIT_REACHED_DISRUPT);
    }
}
