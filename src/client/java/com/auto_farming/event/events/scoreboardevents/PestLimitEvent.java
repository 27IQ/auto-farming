package com.auto_farming.event.events.scoreboardevents;

import java.util.concurrent.ConcurrentHashMap;

import com.auto_farming.event.events.basicevents.BasicScoreboardEvent;
import com.auto_farming.scoreboard.RegexPattern;
import com.auto_farming.scoreboard.RegexResult;

public class PestLimitEvent extends BasicScoreboardEvent {

    public PestLimitEvent(ConcurrentHashMap<RegexPattern, RegexResult> result) {
        super(result);
    }

}
