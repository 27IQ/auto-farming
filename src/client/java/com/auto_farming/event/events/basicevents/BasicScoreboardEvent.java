package com.auto_farming.event.events.basicevents;

import java.util.concurrent.ConcurrentHashMap;

import com.auto_farming.scoreboard.RegexPattern;
import com.auto_farming.scoreboard.RegexResult;

public class BasicScoreboardEvent extends BasicEvent {
    private ConcurrentHashMap<RegexPattern, RegexResult> result;

    public BasicScoreboardEvent(ConcurrentHashMap<RegexPattern, RegexResult> result) {
        super();
        this.result = result;
    }

    public ConcurrentHashMap<RegexPattern, RegexResult> getResult() {
        return result;
    }
}
