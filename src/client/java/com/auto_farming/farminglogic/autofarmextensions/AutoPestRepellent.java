package com.auto_farming.farminglogic.autofarmextensions;

import static com.auto_farming.scoreboard.RegexPattern.PEST_REPELLENT_NONE_PATTERN;
import static com.auto_farming.scoreboard.RegexPattern.PEST_REPELLENT_REGULAR_PATTERN;

import com.auto_farming.event.annotations.Event;
import com.auto_farming.event.events.scoreboardevents.PestRepellentEvent;
import static com.auto_farming.inventory.transactions.AutoPestRepellentTransaction.AUTO_PEST_REPELLENT_TRANSACTION;
import com.auto_farming.scoreboard.RegexResult;

public class AutoPestRepellent {

    @Event(PestRepellentEvent.class)
    public static void managePestRepellent(PestRepellentEvent event) {

        RegexResult prNone = event.getResult().get(PEST_REPELLENT_NONE_PATTERN);
        RegexResult prReg = event.getResult().get(PEST_REPELLENT_REGULAR_PATTERN);

        assert prNone != null && prReg != null;

        if ((prNone.found || prReg.found))
            AUTO_PEST_REPELLENT_TRANSACTION.queueIfAbsent();
    }
}
